package com.lazy.msgtx.core.provide;

import com.lazy.msgtx.core.MessageTransactionContext;
import com.lazy.msgtx.core.MessageTransactionManager;

/**
 * <p>
 * 消息事务信息提供器
 * </p>
 *
 * @author lzy
 * @since 2022/5/28.
 */
public interface AbstractMessageProvide {

    /**
     * 设置消息类型
     *
     * @return
     */
    void setMessageType(String messageType);

    /**
     * 获取消息类型
     *
     * @return
     */
    String getMessageType();

    /**
     * 获取消息ID
     *
     * @return
     */
    String messageId();


    /**
     * 获取消息体
     *
     * @return
     */
    String messageBody();

    /**
     * 业务关键ID，例如销售单号
     *
     * @return
     */
    String bizId();

    /**
     * 获取栈帧
     *
     * @return
     */
    default MessageTransactionContext.MessageStackFrame frame() {
        MessageTransactionContext context = MessageTransactionManager.MESSAGE_TRANSACTION_CONTEXT.get();
        return context.getInvokeStack().peek();
    }

}
