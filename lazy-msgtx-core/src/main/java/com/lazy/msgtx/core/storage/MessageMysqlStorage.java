package com.lazy.msgtx.core.storage;

import com.lazy.msgtx.core.MessageLog;
import com.lazy.msgtx.core.autoconfig.ProjectProperty;
import com.lazy.msgtx.core.common.MsgTxException;
import com.lazy.msgtx.core.provide.MessageProvide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * message_log 幂等策略
 * </p>
 *
 * @author lzy
 * @since 2022/5/29.
 */
@Slf4j
@Component
public class MessageMysqlStorage extends AbstractMessageStorage {


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ProjectProperty property;

    @Override
    @Transactional(readOnly = true)
    public MessageLog load(MessageProvide messageProvide) {

        try {
            return jdbcTemplate.queryForObject("select * from " + property.getTableName() + " where message_id = ? and message_type = ?",
                    (resultSet, i) -> {
                        MessageLog messageLog = new MessageLog();
                        messageLog.setId(resultSet.getLong("id"));
                        messageLog.setPid(resultSet.getLong("pid"));
                        messageLog.setMessageId(resultSet.getString("message_id"));
                        messageLog.setMessageType(resultSet.getString("message_type"));
                        messageLog.setBizId(resultSet.getString("biz_id"));
                        messageLog.setMessageBody(resultSet.getString("message_body"));
                        messageLog.setProcessStatus(resultSet.getString("process_status"));
                        messageLog.setLastUpdatedBy(resultSet.getLong("last_updated_by"));
                        messageLog.setObjectVersionNumber(resultSet.getLong("object_version_number"));
                        messageLog.setRetryCount(resultSet.getInt("retry_count"));
                        messageLog.setCreationDate(resultSet.getDate("creation_date"));
                        messageLog.setLastUpdateDate(resultSet.getDate("last_update_date"));
                        messageLog.setLastUpdatedAccount(resultSet.getString("last_updated_account"));
                        messageLog.setCreatedAccount(resultSet.getString("created_account"));
                        messageLog.setCreatedBy(resultSet.getLong("created_by"));
                        return messageLog;
                    },
                    messageProvide.messageId(), messageProvide.getMessageType()
            );
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MessageLog save(MessageLog messageLog) {
        //设置默认值
        messageLog.setCreatedBy(-1L);
        messageLog.setCreatedAccount("sys");
        messageLog.setCreationDate(new Date());
        messageLog.setLastUpdateDate(new Date());
        messageLog.setLastUpdatedAccount("-1");
        messageLog.setObjectVersionNumber(1L);
        //
        jdbcTemplate.update(
                "INSERT INTO " + property.getTableName() +
                        " (id, pid, biz_id, message_id, message_body, process_status, retry_count, message_type," +
                        " created_by, creation_date, last_updated_by, last_update_date, last_updated_account, " +
                        "created_account, object_version_number)\n" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                messageLog.getId(),
                messageLog.getPid(),
                messageLog.getBizId(),
                messageLog.getMessageId(),
                messageLog.getMessageBody(),
                messageLog.getProcessStatus(),
                messageLog.getRetryCount(),
                messageLog.getMessageType(),
                messageLog.getCreatedBy(),
                messageLog.getCreationDate(),
                messageLog.getLastUpdatedBy(),
                messageLog.getLastUpdateDate(),
                messageLog.getLastUpdatedAccount(),
                messageLog.getCreatedAccount(),
                messageLog.getObjectVersionNumber()
        );
        return messageLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public MessageLog update(MessageLog messageLog) {
        //设置默认值
        messageLog.setLastUpdateDate(new Date());
        messageLog.setLastUpdatedAccount("-1");

        //
        int updateCount = jdbcTemplate.update("UPDATE " + property.getTableName() +
                        " SET  pid=?, biz_id=?, message_body=?, process_status=?, retry_count=?, created_by=?," +
                        " creation_date=?, last_updated_by=?, last_update_date=?, last_updated_account=?, " +
                        "created_account=?, object_version_number=object_version_number+1 " +
                        "WHERE id =? and object_version_number = ?",
                messageLog.getPid(),
                messageLog.getBizId(),
                messageLog.getMessageBody(),
                messageLog.getProcessStatus(),
                messageLog.getRetryCount(),
                messageLog.getCreatedBy(),
                messageLog.getCreationDate(),
                messageLog.getLastUpdatedBy(),
                messageLog.getLastUpdateDate(),
                messageLog.getLastUpdatedAccount(),
                messageLog.getCreatedAccount(),
                messageLog.getId(),
                messageLog.getObjectVersionNumber()
        );

        if (updateCount < 1) {
            throw new MsgTxException("OptimisticLockException 乐观锁异常, 当前对象版本号：" + messageLog.getObjectVersionNumber());
        }
        //累计版本号
        messageLog.setObjectVersionNumber(messageLog.getObjectVersionNumber() + 1);
        return messageLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MessageLog updateMessageBody(MessageLog messageLog) {

        //
        int updateCount = jdbcTemplate.update("UPDATE " + property.getTableName() +
                        " SET  message_body=?, object_version_number=object_version_number+1 " +
                        " WHERE id =? and object_version_number = ?",
                messageLog.getMessageBody(),
                messageLog.getId(),
                messageLog.getObjectVersionNumber()
        );

        if (updateCount < 1) {
            throw new MsgTxException("OptimisticLockException 乐观锁异常, 当前对象版本号：" + messageLog.getObjectVersionNumber());
        }
        //累计版本号
        messageLog.setObjectVersionNumber(messageLog.getObjectVersionNumber() + 1);
        return messageLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MessageLog addRetryCount(MessageLog messageLog) {
        if (messageLog == null) {
            return null;
        }
        messageLog.setRetryCount(messageLog.getRetryCount() == null ? 0 : (messageLog.getRetryCount() + 1));
        return this.update(messageLog);
    }

    @Override
    public void createTable() {
        try {

            if (this.isExistTable()) {
                return;
            }
            String ddl = "CREATE TABLE `" + property.getTableName() + "` (\n" +
                    "  `id` bigint(20) NOT NULL COMMENT '主键',\n" +
                    "  `pid` bigint(20) NOT NULL COMMENT '父级ID',\n" +
                    "  `biz_id` varchar(60) DEFAULT NULL COMMENT '业务关键ID',\n" +
                    "  `message_id` varchar(200) NOT NULL COMMENT '消息ID，幂等去重',\n" +
                    "  `message_type` varchar(60) NOT NULL COMMENT '消息类型',\n" +
                    "  `message_body` text NOT NULL COMMENT '消息体',\n" +
                    "  `process_status` varchar(15) NOT NULL COMMENT '处理状态',\n" +
                    "  `retry_count` int(11) NOT NULL COMMENT '重试次数',\n" +
                    "  `retry_endpoint` varchar(200) DEFAULT NULL COMMENT '重试调用地址',\n" +
                    "  `created_by` varchar(256) DEFAULT NULL COMMENT '创建人',\n" +
                    "  `creation_date` datetime DEFAULT NULL COMMENT '创建时间',\n" +
                    "  `last_updated_by` varchar(256) DEFAULT NULL COMMENT '更新人',\n" +
                    "  `last_update_date` datetime DEFAULT NULL COMMENT '更新时间',\n" +
                    "  `last_updated_account` varchar(32) DEFAULT NULL COMMENT '最后修改人账号',\n" +
                    "  `created_account` varchar(32) DEFAULT NULL COMMENT '创建人账号',\n" +
                    "  `object_version_number` int(11) DEFAULT NULL COMMENT '乐观锁版本号',\n" +
                    "  UNIQUE KEY `message_log_message_id_IDX` (`message_id`,`message_type`),\n" +
                    "  PRIMARY KEY (`id`) /*T![clustered_index] NONCLUSTERED */\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单中心-消息日志表'";
            jdbcTemplate.execute(ddl);
        } catch (Exception e) {

            log.error("创建消息事务表DDL执行异常", e);
        }
    }

    private boolean isExistTable() {

        String isTableSql = String.format("SELECT COUNT(*) as count FROM information_schema.TABLES WHERE " + "table_name = '%s'",
                property.getTableName());
        Map<String, Object> map = jdbcTemplate.queryForMap(isTableSql);
        return Integer.parseInt(map.get("count").toString()) > 0;
    }

    @Override
    public boolean isSupport(StorageType strategy) {
        return StorageType.MYSQL == strategy;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageLog load(Long id) {
        Assert.notNull(id, "id不能为null");
        try {
            return jdbcTemplate.queryForObject("select * from " + property.getTableName() + " where id = ?",
                    (resultSet, i) -> {
                        MessageLog messageLog = new MessageLog();
                        messageLog.setId(resultSet.getLong("id"));
                        messageLog.setPid(resultSet.getLong("pid"));
                        messageLog.setMessageId(resultSet.getString("message_id"));
                        messageLog.setMessageType(resultSet.getString("message_type"));
                        messageLog.setBizId(resultSet.getString("biz_id"));
                        messageLog.setMessageBody(resultSet.getString("message_body"));
                        messageLog.setProcessStatus(resultSet.getString("process_status"));
                        messageLog.setLastUpdatedBy(resultSet.getLong("last_updated_by"));
                        messageLog.setObjectVersionNumber(resultSet.getLong("object_version_number"));
                        messageLog.setRetryCount(resultSet.getInt("retry_count"));
                        messageLog.setCreationDate(resultSet.getDate("creation_date"));
                        messageLog.setLastUpdateDate(resultSet.getDate("last_update_date"));
                        messageLog.setLastUpdatedAccount(resultSet.getString("last_updated_account"));
                        messageLog.setCreatedAccount(resultSet.getString("created_account"));
                        messageLog.setCreatedBy(resultSet.getLong("created_by"));
                        return messageLog;
                    },
                    id
            );
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
    }

}
