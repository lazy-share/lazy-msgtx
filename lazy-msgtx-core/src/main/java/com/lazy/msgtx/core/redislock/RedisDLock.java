package com.lazy.msgtx.core.redislock;

import java.lang.annotation.*;

/**
 * <p>
 * 分布式锁枚举
 * </p>
 *
 * @author lzy
 * @since 2021/6/19.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisDLock {

    /**
     * 锁超时自动释放时间  单位：ms 默认5000ms
     */
    long lockTimeoutMs() default 5000;

    /**
     * 等待重试获取次数，默认重试 20次
     */
    int retryNum() default 20;

    /**
     * 等待重试间隔时间 单位：ms 默认间隔 200ms
     */
    long intervalMs() default 200;

    /**
     * 资源ID
     *
     * @return
     */
    String resourceId() default "";

    /**
     * 资源ID类型，
     *
     * @return {@link ResourceIdType}
     */
    ResourceIdType resourceIdType() default ResourceIdType.CONST;

    /**
     * resourceIdType = ResourceIdType.CONST时，该参数无效，
     * resourceIdType = ResourceIdType.OBJECT_FIELD时，该参数表示对象参数位置，
     * resourceIdType = ResourceIdType.METHOD_PARAM时，该参数表示对象参数位置，
     *
     * @return
     */
    int paramIdx();

    /**
     * 资源ID获取方式
     */
    enum ResourceIdType {
        //固定常量值
        CONST,
        //从对象属性读取
        OBJECT_FIELD,
        //从方法参数获取
        METHOD_PARAM
    }
}
