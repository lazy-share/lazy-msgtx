package com.lazy.msgtx.core.provide;

import com.lazy.msgtx.core.serializer.SerializationFactory;

/**
 * <p>
 * 消息事务信息提供器
 * </p>
 *
 * @author lzy
 * @since 2022/5/28.
 */
public abstract class MessageProvide implements AbstractMessageProvide {

    private String messageType;

    @Override
    public String messageBody() {
        return SerializationFactory.of().serialize(this);
    }

    @Override
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public String getMessageType() {
        return this.messageType;
    }
}
