package com.lazy.msgtx.core.endpoint;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *   递归树
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
@Data
public class MsgTxTree implements Serializable, Comparable<MsgTxTree> {

    private String messageId;
    private String pid;
    private String bizId;
    private String id;
    private String messageBody;
    private String processStatus;
    private Integer retryCount;
    private String messageType;
    private String retryEndpoint;
    private Long createdBy;
    private String createdAccount;
    private Date creationDate;
    private Long lastUpdatedBy;
    private String lastUpdatedAccount;
    private Date lastUpdateDate;
    private List<MsgTxTree> subList = new ArrayList<>();

    @Override
    public int compareTo(MsgTxTree o) {
        return o.lastUpdateDate.compareTo(lastUpdateDate);
    }
}
