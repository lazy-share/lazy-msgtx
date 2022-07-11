package com.lazy.msgtx.example.service;

import com.alibaba.fastjson.JSON;
import com.lazy.msgtx.core.MessageLog;
import com.lazy.msgtx.core.provide.AbstractWarnNotifyProvide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 日志告警  自定义实现类
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Slf4j
@Component
public class MyLogWarnProvide implements AbstractWarnNotifyProvide {

    @Override
    public void notify(MessageLog rootMessageLog) {
        log.error("自定义实现消息事务执行失败告警：{}", JSON.toJSONString(rootMessageLog));
    }
}
