package com.lazy.msgtx.core.redislock;

/**
 * <p>
 * 释放锁异常
 * </p>
 *
 * @author laizhiyuan
 * @since 2021/06/21.
 */
public class ReleaseLockException extends RuntimeException {

	public ReleaseLockException() {
	}

	public ReleaseLockException(String message) {
		super(message);
	}

	public ReleaseLockException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReleaseLockException(Throwable cause) {
		super(cause);
	}

	public ReleaseLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
