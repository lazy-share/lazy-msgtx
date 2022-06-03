package com.lazy.msgtx.core;

import lombok.Data;

import java.util.Date;


/**
 * 订单中心-消息日志表 实体
 *
 * @author lzy 2022-05-27 10:32:10
 */
@Data
public class MessageLog {


    private String messageId;
    private Long pid;
    private String bizId;
    private Long id;
    private String messageBody;
    private String processStatus;
    private Integer retryCount;
    private String messageType;
    private boolean isNew = true;
    private boolean isRetry = false;
    protected Long createdBy;
    protected String createdAccount;
    protected Date creationDate;
    protected Long lastUpdatedBy;
    protected String lastUpdatedAccount;
    protected Date lastUpdateDate;
    private Long objectVersionNumber;


}
