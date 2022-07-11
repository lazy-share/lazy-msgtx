package com.lazy.msgtx.core.beans;

import com.lazy.msgtx.core.common.SpringUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>
 * 应用启动初始化Bean
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
public class AppStartInitBean implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.inject(applicationContext);
    }
}
