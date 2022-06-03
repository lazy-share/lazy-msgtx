package com.lazy.msgtx.core;

import java.lang.annotation.*;

/**
 * <p>
 * 消息事务注解
 * </p>
 *
 * @author lzy
 * @since 2022/5/27.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MessageTransaction {

    /**
     * 消息类型
     *
     * @return
     */
    String messageType();
}
