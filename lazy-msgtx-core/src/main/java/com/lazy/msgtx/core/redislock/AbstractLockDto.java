package com.lazy.msgtx.core.redislock;

import java.io.Serializable;

/**
 * <p>
 * 分布式锁抽象DTO
 * </p>
 *
 * @author laizhiyuan
 * @since 2021/06/21.
 */
public abstract class AbstractLockDto implements Serializable {

	private static final long serialVersionUID = 98765432456L;

	/**
	 * 需要上锁的资源id
	 */
	protected String resourceId;
	/**
	 * s to ms
	 */
	protected long secondToMilli = 1000;


	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		AssertUtils.isBlank(resourceId, "分布式锁异常，resourceId不能为空");
		String LOCK_PRE = "lock_";
		this.resourceId = LOCK_PRE + resourceId;
	}
}
