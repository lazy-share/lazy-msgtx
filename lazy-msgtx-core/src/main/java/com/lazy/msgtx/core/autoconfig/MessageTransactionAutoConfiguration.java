package com.lazy.msgtx.core.autoconfig;

import com.lazy.msgtx.core.beans.AppStartInitBean;
import com.lazy.msgtx.core.beans.AutoCreateTableBean;
import com.lazy.msgtx.core.provide.AbstractWarnNotifyProvide;
import com.lazy.msgtx.core.provide.LogWarnNotifyProvide;
import com.lazy.msgtx.core.storage.MessageStorage;
import com.lazy.msgtx.core.threadpool.MsgTxThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public AppStartInitBean appStartInitBean() {
        log.info("msgtx init bean name appStartInitBean by {}", AppStartInitBean.class.getName());
        return new AppStartInitBean();
    }

    @Bean
    @ConditionalOnProperty(value = "lazy.msgtx.auto-create-table", havingValue = "true")
    public AutoCreateTableBean autoCreateTable(@Autowired MessageStorage messageStorage) {
        log.info("msgtx init bean name autoCreateTable by {}", AutoCreateTableBean.class.getName());
        return new AutoCreateTableBean(messageStorage);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "lazy.msgtx.enable-warn-notify", havingValue = "true")
    public AbstractWarnNotifyProvide warnNotifyProvide() {
        log.info("msgtx init bean name warnNotifyProvide by {} implements", AbstractWarnNotifyProvide.class.getName());
        return new LogWarnNotifyProvide();
    }


    @Bean
    @ConditionalOnProperty(value = "lazy.msgtx.enable-warn-notify", havingValue = "true")
    public ExecutorService warnNotifyThreadPool() {
        log.info("msgtx init bean warnNotifyThreadPool");
        //核心线程数量
        int corePoolSize = 1;
        //最大线程数量(核心线程 + 非核心)
        int maximumPoolSize = 2;
        //线程没有任务执行时最多保持毫秒
        long keepAliveTime = 200L;
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(ProjectProperty.SELF.getWarnNotifyThreadCount()),
                new MsgTxThreadFactory("msgtx-")
        );
    }

}
