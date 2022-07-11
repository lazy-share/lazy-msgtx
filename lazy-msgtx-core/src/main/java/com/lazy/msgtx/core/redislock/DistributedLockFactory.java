package com.lazy.msgtx.core.redislock;

import com.lazy.msgtx.core.common.SpringUtil;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author laizhiyuan
 * @since 2021/06/21.
 * <p>
 * 分布式锁工厂类
 * </p>
 */
public class DistributedLockFactory {


    @SuppressWarnings("all")
    public static RedisDistributedLockFactory ofRedis(RedisOperations<String, String> redisOperations) {

        if (redisOperations == null) {

            return new RedisDistributedLockFactory((RedisOperations<String, String>) SpringUtil.getBean(RedisTemplate.class));
        }
        return new RedisDistributedLockFactory(redisOperations);
    }


}
