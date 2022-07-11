package com.lazy.msgtx.core.provide;

import com.lazy.msgtx.core.MessageTransactionContext;
import com.lazy.msgtx.core.MessageTransactionManager;
import com.lazy.msgtx.core.serializer.SerializationFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 消息事务信息提供器
 * </p>
 *
 * @author lzy
 * @since 2022/5/28.
 */
@Data
@Slf4j
public abstract class MessageProvide implements AbstractMessageProvide {

    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 重试地址
     */
    private String retryEndpoint;

    /**
     * 获取栈帧
     *
     * @return
     */
    public MessageTransactionContext.MessageStackFrame frame() {
        MessageTransactionContext context = MessageTransactionManager.MESSAGE_TRANSACTION_CONTEXT.get();
        return context.getInvokeStack().peek();
    }

    @Override
    public String messageBody() {
        return SerializationFactory.of().serialize(this);
    }

    @Override
    public void beforeProcess() {

    }

    @Override
    public void afterProcess() {

    }
}
