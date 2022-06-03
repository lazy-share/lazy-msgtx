package com.lazy.msgtx.core.autoconfig;

import com.lazy.msgtx.core.common.SerializerType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 配置信息
 * </p>
 *
 * @author lzy
 * @since 2022/6/1.
 */
@Data
@Configuration
@ConfigurationProperties("lazy.msgtx")
public class ProjectProperty {

    public static ProjectProperty SELF;

    @PostConstruct
    public void init() {
        SELF = this;
    }


    /**
     * 表名称
     */
    private String tableName = "message_log";
    /**
     * 自动创建表
     */
    private boolean autoCreateTable = false;
    /**
     * 序列化方式
     */
    private SerializerType serializer = SerializerType.FASTJSON;
}
