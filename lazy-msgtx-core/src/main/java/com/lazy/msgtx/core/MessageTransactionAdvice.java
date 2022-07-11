package com.lazy.msgtx.core;

import com.lazy.msgtx.core.common.MsgTxException;
import com.lazy.msgtx.core.provide.MessageProvide;
import com.lazy.msgtx.core.serializer.SerializationFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * <p>
 * 消息事务切面
 * </p>
 *
 * @author lzy
 * @since 2022/5/27.
 */
@Aspect
@Slf4j
@Component
@Order
public class MessageTransactionAdvice {


    private ApplicationContext applicationContext;

    @Autowired
    private MessageTransactionManager transactionManager;

    @Autowired
    private MessageLogService messageLogService;


    @Around(value = "@annotation(messageTransaction)")
    public Object messageTransaction(ProceedingJoinPoint joinPoint, MessageTransaction messageTransaction) throws Throwable {

        //切面参数
        Object[] args = joinPoint.getArgs();
        Assert.isTrue((args.length == 1), "方法参数必须只能1个");
        Assert.isTrue((ClassUtils.hasConstructor(args[0].getClass())), "方法参数必须提供无参构造器");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (!method.isAnnotationPresent(MessageTransaction.class)) {
            return joinPoint.proceed(args);
        }

        //获取消息提供器
        MessageProvide messageProvide = this.takeProvide(joinPoint);
        messageProvide.setMessageType(messageTransaction.messageType());
        messageProvide.setRetryEndpoint(String.format("%s#%s#%s",
                joinPoint.getTarget().getClass().getName(), method.getName(), method.getParameterTypes()[0].getName()));

        try {
            //开启事务
            transactionManager.begin(messageProvide);

            //执行业务方法
            Object bizResult = messageLogService.doWork(joinPoint, messageProvide);

            //提交事务
            transactionManager.commit(messageProvide);
            return bizResult;

        } catch (Throwable e) {

            log.info("执行业务逻辑发生异常, 当前参数{}", SerializationFactory.of().serialize(args), e);
            //回滚事务
            transactionManager.rollback(messageProvide);
            throw e;
        } finally {

            //清除上下文
            transactionManager.clearContext(messageProvide);
        }
    }

    private MessageProvide takeProvide(ProceedingJoinPoint joinPoint) throws Throwable {
        //
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof MessageProvide) {
                return (MessageProvide) arg;
            }
        }
        throw new MsgTxException("消息事务注解，方法没有找到任一实现" + MessageProvide.class.getName() + "接口的参数");
    }


}
