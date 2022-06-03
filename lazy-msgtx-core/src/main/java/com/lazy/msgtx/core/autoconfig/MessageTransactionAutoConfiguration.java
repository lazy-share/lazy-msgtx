package com.lazy.msgtx.core.autoconfig;

import com.lazy.msgtx.core.beans.AutoCreateTableBean;
import com.lazy.msgtx.core.storage.MessageStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 自动配置
 * </p>
 *
 * @author lzy
 * @since 2022/5/31.
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.lazy.msgtx.core")
public class MessageTransactionAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "lazy.msgtx.auto-create-table", havingValue = "true")
    public AutoCreateTableBean autoCreateTable(@Autowired MessageStorage messageStorage) {
        log.info("init bean name autoCreateTable by @com.lazy.msgtx.core.autoconfig.AutoCreateTableBean");
        return new AutoCreateTableBean(messageStorage);
    }

}
