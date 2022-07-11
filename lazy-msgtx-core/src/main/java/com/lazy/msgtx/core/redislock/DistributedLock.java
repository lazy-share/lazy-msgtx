package com.lazy.msgtx.core.redislock;

/**
 * @author laizhiyuan
 * @since 2021/06/21.
 * <p>
 * 分布式锁接口
 * </p>
 */
public interface DistributedLock {

	/**
	 * 获取分布式锁
	 *
	 * @return true:成功  false:失败
	 */
	boolean acquire();

	/**
	 * 释放分布式锁
	 *
	 * @return true:成功  false:失败
	 */
	boolean release();


}
