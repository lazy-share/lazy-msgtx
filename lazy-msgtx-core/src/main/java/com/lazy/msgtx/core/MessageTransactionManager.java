package com.lazy.msgtx.core;

import com.lazy.msgtx.core.autoconfig.ProjectProperty;
import com.lazy.msgtx.core.provide.AbstractWarnNotifyProvide;
import com.lazy.msgtx.core.provide.MessageProvide;
import com.lazy.msgtx.core.storage.MessageStorage;
import com.lazy.msgtx.core.threadpool.WarnNotifyRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.ExecutorService;

/**
 * <p>
 * 消息事务管理器
 * </p>
 *
 * @author lzy
 * @since 2022/5/30.
 */
@Slf4j
@Component
public class MessageTransactionManager {

    /**
     * 配置
     */
    @Autowired
    private ProjectProperty property;
    /**
     * 告警线程池
     */
    @Autowired(required = false)
    @Qualifier("warnNotifyThreadPool")
    private ExecutorService warnNotifyThreadPool;
    /**
     * 消息日志服务类
     */
    @Autowired
    private MessageStorage messageStorage;
    /**
     * 告警通知
     */
    @Autowired(required = false)
    private AbstractWarnNotifyProvide warnNotifyProvide;

    /**
     * Spring 事务管理器
     */
    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * 当前线程消息事务上下文 一个事务根对应一个上下文
     */
    public static final ThreadLocal<MessageTransactionContext> MESSAGE_TRANSACTION_CONTEXT = new ThreadLocal<>();


    /**
     * 开启事务
     *
     * @param provide 消息信息提供器
     */
    public void begin(MessageProvide provide) {

        //获取上下文
        MessageTransactionContext context = this.takeContext();

        //调用方法入栈
        context.inStack(provide);

        //第一个入栈时开始事务
        if (context.isFirst()) {
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            context.setTransactionStatus(transactionManager.getTransaction(defaultTransactionDefinition));
            log.info("消息事务根：{} 消息类型：{} 开始事务", context.getRootMessagePk(), provide.getMessageType());
        }

    }

    /**
     * 提交事务
     *
     * @param provide 消息信息提供器
     */
    public void commit(MessageProvide provide) {
        //获取上下文
        MessageTransactionContext context = MESSAGE_TRANSACTION_CONTEXT.get();
        if (context != null) {
            //调用方法出栈
            MessageTransactionContext.MessageStackFrame frame = context.outStack(provide);
            //更新消息体
            if (!context.isLast()) {
                //新创建和重试的时候，才更新消息体
                if (frame.getMessageLog().isNew() || frame.getMessageLog().isRetry()) {
                    frame.updateMessageBody(provide.messageBody());
                    log.info("消息事务根：{} 消息类型：{} 更新消息体", context.getRootMessagePk(), provide.getMessageType());
                }
            }
            //最后一个出栈时，提交事务
            if (context.isLast()) {

                //提交事务
                transactionManager.commit(context.getTransactionStatus());
                log.info("消息事务根：{} 消息类型：{} 提交事务", context.getRootMessagePk(), provide.getMessageType());

                //执行钩子函数
                this.afterProcess(provide);
            }
        }

    }

    private void afterProcess(MessageProvide messageProvide) {
        try {
            messageProvide.afterProcess();
        } catch (Exception e) {
            log.error("messageType：{} messageId：{} 执行回调方法[ afterProcess ]发生异常",
                    messageProvide.getMessageType(), messageProvide.messageBody(), e);
        }
    }

    /**
     * 回滚事务
     *
     * @param provide 消息信息提供器
     */
    public void rollback(MessageProvide provide) {

        //获取上下文
        MessageTransactionContext context = MESSAGE_TRANSACTION_CONTEXT.get();
        if (context != null) {
            //调用方法出栈
            MessageTransactionContext.MessageStackFrame frame = context.outStack(provide);
            //最后一个出栈时，回滚事务
            if (context.isLast()) {
                transactionManager.rollback(context.getTransactionStatus());
                log.info("消息事务根：{} 消息类型：{} 回滚事务", context.getRootMessagePk(), provide.getMessageType());
                //异步发送告警通知
                if (property.isEnableWarnNotify()) {
                    this.asyncWarnNotify(frame.getMessageLog());
                }
            }
        }

    }

    /**
     * 异步哦通知失败告警
     *
     * @param messageLog 消息日志
     */
    private void asyncWarnNotify(MessageLog messageLog) {
        try {
            warnNotifyThreadPool.execute(new WarnNotifyRunner(warnNotifyProvide, messageLog));
        } catch (Throwable ignored) {
            //忽略由于线程池产生的任何异常
        }
    }

    /**
     * 清除上下文
     */
    public void clearContext(MessageProvide provide) {
        //获取上下文
        MessageTransactionContext context = MESSAGE_TRANSACTION_CONTEXT.get();
        if (context != null) {
            if (context.isLast()) {
                MESSAGE_TRANSACTION_CONTEXT.remove();
                log.info("消息事务根：{} 消息类型：{} 清除上下文", context.getRootMessagePk(), provide.getMessageType());
            }
        }
    }

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    private MessageTransactionContext takeContext() {
        MessageTransactionContext context = MESSAGE_TRANSACTION_CONTEXT.get();
        if (context == null) {
            context = new MessageTransactionContext(messageStorage);
            MESSAGE_TRANSACTION_CONTEXT.set(context);
        }
        return context;
    }

}
