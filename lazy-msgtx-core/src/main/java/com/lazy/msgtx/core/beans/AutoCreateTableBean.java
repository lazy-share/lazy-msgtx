package com.lazy.msgtx.core.beans;

import com.lazy.msgtx.core.storage.MessageStorage;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>
 *  自动创建表
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
public class AutoCreateTableBean implements InitializingBean {

    private MessageStorage messageStorage;

    public AutoCreateTableBean(MessageStorage messageStorage) {
        this.messageStorage = messageStorage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        messageStorage.createTable();
    }
}
