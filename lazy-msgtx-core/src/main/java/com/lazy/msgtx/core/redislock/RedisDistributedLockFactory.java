package com.lazy.msgtx.core.redislock;


import org.springframework.data.redis.core.RedisOperations;

/**
 * @author laizhiyuan
 * @since 2021/06/21.
 * <p>
 * redis分布式锁工厂类
 * </p>
 */
public class RedisDistributedLockFactory {


    private RedisOperations<String, String> redisOperations;
    private RedisLockDto dto;

    public RedisDistributedLockFactory(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
        this.dto = new RedisLockDto();
    }

    public RedisDistributedLockFactory setLockTimeoutMilli(long lockTimeoutMilli) {
        this.dto.setLockTimeoutMilli(lockTimeoutMilli);
        return this;
    }

    public RedisDistributedLockFactory setRetryNum(int retryNum) {
        this.dto.setRetryNum(retryNum);
        return this;
    }

    public RedisDistributedLockFactory setIntervalMilli(long intervalMilli) {
        this.dto.setIntervalMilli(intervalMilli);
        return this;
    }

    public RedisDistributedLockFactory setResourceId(String resourceId) {
        this.dto.setResourceId(resourceId);
        return this;
    }

    public DistributedLock builder() {
        return new RedisDistributedLock(redisOperations, this.dto);
    }
}
