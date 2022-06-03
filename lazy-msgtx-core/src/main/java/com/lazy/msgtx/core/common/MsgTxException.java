package com.lazy.msgtx.core.common;

/**
 * <p>
 * 消息事务异常
 * </p>
 *
 * @author lzy
 * @since 2022/5/31.
 */
public class MsgTxException extends RuntimeException {

    public MsgTxException() {
    }

    public MsgTxException(String message) {
        super(message);
    }

    public MsgTxException(String message, Throwable cause) {
        super(message, cause);
    }

    public MsgTxException(Throwable cause) {
        super(cause);
    }

    public MsgTxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
