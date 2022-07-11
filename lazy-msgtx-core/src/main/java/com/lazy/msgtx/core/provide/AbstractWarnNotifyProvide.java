package com.lazy.msgtx.core.provide;

import com.lazy.msgtx.core.MessageLog;

/**
 * <p>
 * 告警通知提供器
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
public interface AbstractWarnNotifyProvide {

    /**
     * 告警通知
     *
     * @param rootMessageLog 根消息日志
     */
    void notify(MessageLog rootMessageLog);

}
