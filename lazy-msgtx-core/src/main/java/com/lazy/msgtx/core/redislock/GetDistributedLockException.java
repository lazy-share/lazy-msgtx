package com.lazy.msgtx.core.redislock;

/**
 * <p>
 * 获取分布式锁失败异常
 * </p>
 *
 * @author laizhiyuan
 * @since 2021/06/21.
 */
public class GetDistributedLockException extends RuntimeException {

    public GetDistributedLockException() {
    }

    public GetDistributedLockException(String message) {
        super(message);
    }

    public GetDistributedLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetDistributedLockException(Throwable cause) {
        super(cause);
    }

    public GetDistributedLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
