package com.lazy.msgtx.core;

import com.lazy.msgtx.core.common.Const;
import com.lazy.msgtx.core.common.IdUtil;
import com.lazy.msgtx.core.provide.MessageProvide;
import com.lazy.msgtx.core.storage.MessageStorage;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.TransactionStatus;

import java.util.Stack;

/**
 * <p>
 * 消息事务上下文
 * </p>
 *
 * @author lzy
 * @since 2022/6/2.
 */
@Slf4j
@Data
public class MessageTransactionContext {


    /**
     * 消息存储器
     */
    private MessageStorage messageStorage;


    /**
     * 默认根
     */
    private Long rootPid = Long.parseLong("-1");

    /**
     * 消息根的主键
     */
    private Long rootMessagePk;
    /**
     * 当前消息PID
     */
    private Long currentMessagePid = rootPid;
    /**
     * 消息调用栈
     */
    private MessageStack invokeStack = new MessageStack();
    /**
     * 事务状态
     */
    private TransactionStatus transactionStatus;
    /**
     * 上一个消息体
     */
    private String preMessageBody;

    public MessageTransactionContext(MessageStorage messageStorage) {
        this.messageStorage = messageStorage;
    }

    public boolean isComplete() {
        return transactionStatus.isCompleted();
    }


    public boolean isEmpty() {
        return invokeStack.isEmpty();
    }

    public boolean isFirst() {
        return invokeStack.size() == 1;
    }

    public boolean isLast() {
        return invokeStack.isEmpty();
    }

    /**
     * 入栈
     *
     * @param provide 消息提供器
     */
    public void inStack(MessageProvide provide) {

        //实例化栈帧
        MessageStackFrame frame = new MessageStackFrame(provide);

        //记录根ID
        if (this.isEmpty()) {
            this.rootMessagePk = frame.currentMessagePk;
        }

        //记录当前栈帧的pid
        if (!this.isEmpty()) {
            frame.currentMessagePid = this.rootMessagePk;
            this.currentMessagePid = this.rootMessagePk;
        }
        frame.getMessageLog().setPid(this.currentMessagePid);

        //入栈
        this.invokeStack.push(frame);
        log.info("消息事务根：{} 入栈：{}", this.rootMessagePk, provide.getMessageType());
    }

    /**
     * 出栈
     *
     * @param provide 消息提供器
     */
    public MessageStackFrame outStack(MessageProvide provide) {

        MessageStack stack = this.invokeStack;
        if (!stack.isEmpty()) {
            //出栈
            MessageStackFrame frame = stack.pop();
            log.info("消息事务根：{} 出栈：{}", this.rootMessagePk, provide.getMessageType());
            return frame;
        }
        return null;
    }

    /**
     * 消息栈
     */
    public static class MessageStack extends Stack<MessageStackFrame> {

    }

    /**
     * 消息栈帧
     */
    @Getter
    public class MessageStackFrame {

        /**
         * 提供器
         */
        private MessageProvide provide;
        /**
         * 消息日志
         */
        private MessageLog messageLog;
        /**
         * 当前入栈消息的主键
         */
        private Long currentMessagePk;
        /**
         * 当前入栈消息的PID
         */
        private Long currentMessagePid;

        /**
         * 更新消息体
         *
         * @param messageBody
         */
        public void updateMessageBody(String messageBody) {
            messageLog.setMessageBody(messageBody);
            messageStorage.updateMessageBody(messageLog);
        }

        public MessageStackFrame(MessageProvide provide) {
            this.provide = provide;
            messageLog = messageStorage.load(provide);
            if (messageLog != null) {
                messageLog.setNew(false);
                currentMessagePk = messageLog.getId();
                currentMessagePid = messageLog.getPid();
                messageLog.setRetry(Const.N.equals(messageLog.getProcessStatus()));
            } else {
                //初始化messageLog
                currentMessagePk = IdUtil.DEFAULT_SNOWFLAKE.nextId();
                messageLog = new MessageLog();
                messageLog.setId(currentMessagePk);
                messageLog.setMessageId(provide.messageId());
                messageLog.setMessageType(provide.getMessageType());
                messageLog.setRetryCount(1);
                messageLog.setBizId(provide.bizId());
                messageLog.setProcessStatus(Const.N);
                messageLog.setMessageBody(provide.messageBody());
                messageLog.setRetryEndpoint(provide.getRetryEndpoint());
            }
        }
    }
}
