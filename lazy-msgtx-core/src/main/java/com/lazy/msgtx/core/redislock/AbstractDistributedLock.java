package com.lazy.msgtx.core.redislock;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author laizhiyuan
 * @since 2021/06/21.
 * <p>
 * 抽象分布式锁
 * </p>
 */
public abstract class AbstractDistributedLock implements DistributedLock {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 获取分布式锁
	 *
	 * @return true:成功  false:失败
	 */
	@Override
	public boolean acquire() {
		return this.doAcquire();
	}

	/**
	 * 释放分布式锁
	 *
	 * @return true:成功  false:失败
	 */
	@Override
	public boolean release() {
		return this.doRelease();
	}

	/**
	 * 获取分布式锁
	 *
	 * @return true:成功  false:失败
	 */
	protected abstract boolean doAcquire();

	/**
	 * 释放分布式锁
	 *
	 * @return true:成功  false:失败
	 */
	protected abstract boolean doRelease();

}
