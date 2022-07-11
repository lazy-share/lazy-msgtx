package com.lazy.msgtx.core.redislock;


/**
 * <p>
 * redis lock impl dto
 * </p>
 *
 * @author laizhiyuan
 * @since 2021/06/21.
 */
public class RedisLockDto extends AbstractLockDto {

	private static final long serialVersionUID = 8898765432456L;

	/**
	 * 锁超时时间  单位：ms 默认30s
	 */
	private long lockTimeoutMilli = 30 * secondToMilli;

	/**
	 * 重试次数，默认重试 5次
	 */
	private int retryNum = 5;

	/**
	 * 重试间隔时间 单位：ms 默认间隔 100ms
	 */
	private long intervalMilli = 100;

	/**
	 * 获取锁后锁标示符
	 */
	private String lockId;

	public String getLockId() {
		return lockId;
	}

	public RedisLockDto setLockId(String lockId) {
		this.lockId = lockId;
		return this;
	}

	public long getLockTimeoutMilli() {
		return lockTimeoutMilli;
	}

	public RedisLockDto setLockTimeoutMilli(long lockTimeoutMilli) {
		this.lockTimeoutMilli = lockTimeoutMilli;
		return this;
	}

	public int getRetryNum() {
		return retryNum;
	}

	public RedisLockDto setRetryNum(int retryNum) {
		this.retryNum = retryNum;
		return this;
	}

	public long getIntervalMilli() {
		return intervalMilli;
	}

	public RedisLockDto setIntervalMilli(long intervalMilli) {
		this.intervalMilli = intervalMilli;
		return this;
	}
}
