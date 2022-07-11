package com.lazy.msgtx.core.threadpool;

import com.lazy.msgtx.core.MessageLog;
import com.lazy.msgtx.core.provide.AbstractWarnNotifyProvide;

/**
 * <p>
 * 告警线程工作类
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
public class WarnNotifyRunner implements Runnable {

    private final MessageLog messageLog;
    private final AbstractWarnNotifyProvide notifyProvide;

    public WarnNotifyRunner(AbstractWarnNotifyProvide notifyProvide, MessageLog messageLog) {
        this.notifyProvide = notifyProvide;
        this.messageLog = messageLog;
    }

    @Override
    public void run() {
        notifyProvide.notify(messageLog);
    }
}
