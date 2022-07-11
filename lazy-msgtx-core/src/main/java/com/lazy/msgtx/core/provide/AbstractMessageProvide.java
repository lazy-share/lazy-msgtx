package com.lazy.msgtx.core.provide;

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
     * 根消息保存之后，开始处理业务之前
     *
     * @param
     */
    void beforeProcess();

    /**
     * 整个根业务处理成功之后
     */
    void afterProcess();

}
