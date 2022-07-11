package com.lazy.msgtx.core.redislock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p>
 * 分布式锁切面
 * </p>
 *
 * @author lzy
 * @since 2021/6/19.
 */
@Aspect
@Component
@Slf4j
public class DistributedLockAspect implements InitializingBean {

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    @Around("@annotation(redisDLock)")
    public Object around(ProceedingJoinPoint point, RedisDLock redisDLock) throws Throwable {

        String resourceId = redisDLock.resourceId();
        if (RedisDLock.ResourceIdType.METHOD_PARAM == redisDLock.resourceIdType()) {
            Object[] args = point.getArgs();
            if ((args.length - 1) < redisDLock.paramIdx()) {
                throw new GetDistributedLockException("分布式锁切面异常，注解属性paramIdx超过方法参数数组下标");
            }
            Object resourceIdVal = args[redisDLock.paramIdx()];
            if (resourceIdVal == null) {
                throw new GetDistributedLockException("分布式锁切面异常，resourceId参数值（args[redisDLock.paramIdx()]）不能为null");
            }
            try {
                resourceId = String.valueOf(resourceIdVal);
            } catch (Exception e) {
                throw new GetDistributedLockException("分布式锁切面异常，转换resourceId（String.valueOf(args[redisDLock.paramIdx()])）发生异常", e);
            }
        } else if (RedisDLock.ResourceIdType.OBJECT_FIELD == redisDLock.resourceIdType()) {
            Object[] args = point.getArgs();
            if ((args.length - 1) < redisDLock.paramIdx()) {
                throw new GetDistributedLockException("分布式锁切面异常，注解属性paramIdx超过方法参数数组下标");
            }
            Object resourceIdObj = args[redisDLock.paramIdx()];
            if (resourceIdObj == null) {
                throw new GetDistributedLockException("分布式锁切面异常，resourceId参数值（args[redisDLock.paramIdx()]）不能为null");
            }
            try {
                Method method = resourceIdObj.getClass().getMethod(resourceId);
                boolean isAccessible = method.isAccessible();
                if (!isAccessible) {
                    method.setAccessible(true);
                }
                Object resourceIdVal = method.invoke(resourceIdObj);
                resourceId = String.valueOf(resourceIdVal);
            } catch (Exception e) {
                throw new GetDistributedLockException("分布式锁切面异常，转换resourceId（args[redisDLock.paramIdx()].invoke(...)）发生异常", e);
            }
        }

        //构建分布式锁
        RedisDistributedLockFactory redisDistributedLockFactory = DistributedLockFactory.ofRedis(redisTemplate);
        DistributedLock distributedLock = redisDistributedLockFactory
                .setResourceId(resourceId)
                .setRetryNum(redisDLock.retryNum())
                .setIntervalMilli(redisDLock.intervalMs())
                .setLockTimeoutMilli(redisDLock.lockTimeoutMs())
                .builder();

        if (!distributedLock.acquire()) {
            throw new GetDistributedLockException("获取分布式锁失败，当前有其他程序正在处理：" + resourceId);
        }

        try {
            //执行方法
            return point.proceed();
        } finally {
            //释放锁
            distributedLock.release();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Activate Redis Distributed Lock， Use @RedisDLock to define the need method");
    }
}
