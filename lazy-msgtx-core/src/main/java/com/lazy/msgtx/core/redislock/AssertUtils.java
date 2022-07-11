package com.lazy.msgtx.core.redislock;

/**
 * <p>
 * 断言工具
 * </p>
 *
 * @author lzy
 * @since 2021/6/20.
 */
public class AssertUtils {

    public static void isNull(Object obj, String errorMsg) {
        if (obj == null) {
            throw new RuntimeException(errorMsg);
        }
    }

    public static void isBlank(String obj, String errorMsg) {
        if (obj == null || "".equals(obj)) {
            throw new RuntimeException(errorMsg);
        }
    }
}
