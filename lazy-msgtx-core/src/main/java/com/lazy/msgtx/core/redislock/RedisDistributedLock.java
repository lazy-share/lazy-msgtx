package com.lazy.msgtx.core.redislock;


import com.lazy.msgtx.core.common.IdUtil;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author laizhiyuan
 * @since 2021/06/21.
 * <p>
 * Redis 实现分布式锁  集群多主时，在主切换情况下会有锁失效问题
 * </p>
 */
public class RedisDistributedLock extends AbstractRedisDistributedLock {

    private final RedisOperations<String, String> redisOperations;
    private final RedisLockDto dto;
    private final Long EXEC_FAIL = -10L;

    //定义获取锁的lua脚本
    private final static DefaultRedisScript<Long> LOCK_LUA_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('setnx', KEYS[1], KEYS[2]) == 1 then return redis.call('pexpire', KEYS[1], ARGV[1]) else return -10 end"
            , Long.class
    );

    //定义释放锁的lua脚本
    private final static DefaultRedisScript<Long> RELEASELOCK_LUA_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return -10 end"
            , Long.class
    );


    public RedisDistributedLock(RedisOperations<String, String> redisOperations, RedisLockDto dto) {

        AssertUtils.isNull(redisOperations, "redisOperations dot not null");
        AssertUtils.isNull(dto, "dto dot not null");
        AssertUtils.isBlank(dto.getResourceId(), "resourceId dot not blank");
        this.redisOperations = redisOperations;
        this.dto = dto;
    }

    /**
     * 获取一个UUID
     *
     * @return
     */
    private String getUuid() {
        return IdUtil.DEFAULT_SNOWFLAKE.nextId() + "";
    }

    /**
     * 获取分布式锁
     *
     * @return true:成功  false:失败
     */
    @Override
    protected boolean doAcquire() {
        //锁标识符
        if (StringUtils.isEmpty(dto.getLockId())) {
            dto.setLockId(this.getUuid());
        }

        List<String> keys = Arrays.asList(dto.getResourceId(), dto.getLockId());
        Long result = redisOperations.execute(LOCK_LUA_SCRIPT, keys, String.valueOf(dto.getLockTimeoutMilli()));

        //获取失败，下面根据配置开始重试
        int i = 0;
        while (EXEC_FAIL.equals(result) && i < dto.getRetryNum()) {
            if (dto.getIntervalMilli() > 0) {
                try {
                    Thread.sleep(dto.getIntervalMilli());
                } catch (InterruptedException ex) {
                    logger.error("重试获取分布式锁失败，放弃获取", ex);
                    break;
                }
            }
            result = redisOperations.execute(LOCK_LUA_SCRIPT, keys, String.valueOf(dto.getLockTimeoutMilli()));
            i++;
        }
        return !EXEC_FAIL.equals(result);
    }

    /**
     * 释放分布式锁
     *
     * @return true:成功  false:失败
     */
    @Override
    protected boolean doRelease() {


        Long result = null;
        try {

            List<String> keys = Arrays.asList(dto.getResourceId(), dto.getLockId());
            result = redisOperations.execute(RELEASELOCK_LUA_SCRIPT, keys);

        } catch (Exception e) {

            logger.error("release lock error", e);
        }

        return !EXEC_FAIL.equals(result);
    }
}
