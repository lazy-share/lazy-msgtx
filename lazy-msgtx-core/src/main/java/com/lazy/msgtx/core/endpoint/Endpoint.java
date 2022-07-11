package com.lazy.msgtx.core.endpoint;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * <p>
 *  暴露端点
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
@RestController
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Endpoint {

}
