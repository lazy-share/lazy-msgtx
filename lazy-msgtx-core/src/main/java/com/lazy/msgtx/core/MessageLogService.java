package com.lazy.msgtx.core;

import com.lazy.msgtx.core.common.Const;
import com.lazy.msgtx.core.provide.MessageProvide;
import com.lazy.msgtx.core.serializer.SerializationFactory;
import com.lazy.msgtx.core.storage.MessageStorage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/5/31.
 */
@Slf4j
@Service
public class MessageLogService {

    @Autowired
    private MessageStorage messageStorage;

    @Autowired
    private PlatformTransactionManager transactionManager;


    public Object doWork(ProceedingJoinPoint joinPoint, MessageProvide messageProvide) throws Throwable {

        Assert.notNull(messageProvide.messageId(), "messageId值不能为空");
        Assert.hasText(messageProvide.getMessageType(), "messageType值不能为空");
        Assert.notNull(messageProvide.messageBody(), "messageBody值不能为空");

        //同步执行业务方法
        Object[] args = joinPoint.getArgs();

        MessageLog dbMessageLog = messageProvide.frame().getMessageLog();
        if (this.isIdempotent(dbMessageLog)) {

            //把当前报文作为下一个执行方法的参数
            BeanUtils.copyProperties(SerializationFactory.of().deserialize(dbMessageLog.getMessageBody(), args[0].getClass()), args[0]);
            log.info("消息事务根：{} 消息类型：{} 拦截重复报文，不执行业务方法，当前消息体 {}", dbMessageLog.getPid(),
                    messageProvide.getMessageType(), messageProvide.messageBody());
            return null;
        }

        //数据库不存在，则新建一条，否则累计重试次数
        final MessageLog ofFinalMessageLog = this.saveOrUpdate(dbMessageLog);

        //回调钩子函数
        this.beforeProcess(messageProvide);

        //处理业务逻辑
        if (MessageTransactionManager.MESSAGE_TRANSACTION_CONTEXT.get().isFirst()) {
            return this.rootCall(joinPoint, args, ofFinalMessageLog);
        }
        return this.subCall(joinPoint, args, ofFinalMessageLog);
    }

    private void beforeProcess(MessageProvide messageProvide) {
        try {
            messageProvide.beforeProcess();
        } catch (Exception e) {
            log.error("messageType：{} messageId：{} 执行回调方法[ beforeProcess ]发生异常",
                    messageProvide.getMessageType(), messageProvide.messageBody(), e);
        }
    }

    public MessageLog saveOrUpdate(MessageLog messageLog) {

        if (messageLog.isNew()) {

            return messageStorage.save(messageLog);
        }

        return messageStorage.addRetryCount(messageLog);
    }

    /**
     * 考虑seata事务，这里需要绑定并手动事务来保证一致性
     *
     * @param joinPoint  切入点
     * @param args       参数
     * @param messageLog 消息日志
     * @return
     */
    public Object subCall(ProceedingJoinPoint joinPoint, Object[] args, MessageLog messageLog) throws Throwable {

        //新开事务，使消息日志处理状态processStatus修改跟业务处理绑定起来
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
        try {
            //
            this.successProcess(messageLog);
            //把当前报文作为下一个执行方法的参数
            BeanUtils.copyProperties(SerializationFactory.of().deserialize(messageLog.getMessageBody(), args[0].getClass()), args[0]);
            Object bizResult = joinPoint.proceed(args);
            //提交
            transactionManager.commit(status);
            log.info("消息事务根：{} 消息类型：{} 提交保存消息事务", messageLog.getPid(), messageLog.getMessageType());
            return bizResult;
        } catch (Throwable ex) {
            log.info("消息事务根：{} 消息类型：{} 回滚更新消息为成功状态的事务", messageLog.getPid(), messageLog.getMessageType(), ex);
            //回滚
            transactionManager.rollback(status);
            throw ex;
        }
    }

    public Object rootCall(ProceedingJoinPoint joinPoint, Object[] args, MessageLog messageLog) throws Throwable {
        //
        this.successProcess(messageLog);
        //把当前报文作为下一个执行方法的参数
        BeanUtils.copyProperties(SerializationFactory.of().deserialize(messageLog.getMessageBody(), args[0].getClass()), args[0]);
        return joinPoint.proceed(args);
    }


    /**
     * 是否幂等
     *
     * @param messageLog
     * @return
     */
    public boolean isIdempotent(MessageLog messageLog) {
        return messageLog != null && Const.Y.equals(messageLog.getProcessStatus());
    }

    public Void successProcess(MessageLog messageLog) {
        messageLog.setProcessStatus(Const.Y);
        messageStorage.update(messageLog);
        return null;
    }

}
