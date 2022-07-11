package com.lazy.msgtx.core.provide;

import com.lazy.msgtx.core.MessageLog;
import com.lazy.msgtx.core.serializer.SerializationFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 默认日志打印方式告警提供器实现类
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Slf4j
public class LogWarnNotifyProvide implements AbstractWarnNotifyProvide {

    @Override
    public void notify(MessageLog rootMessageLog) {

        log.warn("消息事务框架触发失败告警：{}", SerializationFactory.of().serialize(rootMessageLog));
    }
}
