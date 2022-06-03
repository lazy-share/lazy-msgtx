package com.lazy.msgtx.core.storage;

import com.lazy.msgtx.core.MessageLog;
import com.lazy.msgtx.core.provide.MessageProvide;

/**
 * <p>
 * 抽象幂等策略执行类
 * </p>
 *
 * @author lzy
 * @since 2022/5/29.
 */
public interface MessageStorage {

    /**
     * 创建表
     */
    void createTable();

    /**
     * 是否支持
     *
     * @param strategy 策略
     * @return true:支持  false:不支持
     */
    boolean isSupport(StorageType strategy);

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    MessageLog load(Long id);

    /**
     * 查询
     *
     * @param messageProvide
     * @return
     */
    MessageLog load(MessageProvide messageProvide);

    /**
     * 创建
     *
     * @param messageLog
     * @return
     */
    MessageLog save(MessageLog messageLog);

    /**
     * 更新
     *
     * @param messageLog
     * @return
     */
    MessageLog update(MessageLog messageLog);

    /**
     * 更新消息体
     *
     * @param messageLog
     * @return
     */
    MessageLog updateMessageBody(MessageLog messageLog);

    /**
     * 添加重试次数
     *
     * @param messageLog
     * @return
     */
    MessageLog addRetryCount(MessageLog messageLog);

}
