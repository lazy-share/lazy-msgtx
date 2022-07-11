package com.lazy.msgtx.core.endpoint;

import com.lazy.msgtx.core.common.MsgTxException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * <p>
 *  分页响应
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
@Slf4j
@Data
public class PageResponse {

    //一页显示的记录数
    private int pageSize;
    //记录总数
    private int totalRows;
    //总页数
    private int totalPages;
    //当前页码
    private int currentPage;
    //起始行数
    private int startIndex;
    //结束行数
    private int lastIndex;
    //结果集存放List
    private List resultList;
    //JdbcTemplate jTemplate
    private JdbcTemplate jTemplate;

    public PageResponse() {
    }

    /**
     * 分页构造函数
     *
     * @param sql         根据传入的sql语句得到一些基本分页信息
     * @param params      参数列表
     * @param pageRequest 分页参数对象
     * @param jTemplate   JdbcTemplate实例
     */
    public PageResponse(String sql, Object[] params, PageRequest pageRequest, JdbcTemplate jTemplate) {
        if (jTemplate == null) {
            throw new MsgTxException("com.deity.ranking.util.PageResponse.jTemplate is null,please initial it first. ");
        } else if (sql == null || sql.equals("")) {
            throw new MsgTxException("com.deity.ranking.util.PageResponse.sql is empty,please initial it first. ");
        }
        //设置每页显示记录数
        setPageSize(pageRequest.getSize());
        //设置要显示的页数
        setCurrentPage(pageRequest.getPage());
        //计算总记录数
        StringBuffer totalSQL = new StringBuffer(" SELECT count(*) FROM ( ");
        totalSQL.append(sql);
        totalSQL.append(" ) totalTable ");
        log.info("查询消息事务结果SQL：{}", sql);
        log.info("查询消息事务总数SQL：{}", totalSQL);
        //总记录数
        setTotalRows(jTemplate.queryForObject(totalSQL.toString(), params, Integer.class));
        //计算总页数
        setTotalPages();
        //计算起始行数
        setStartIndex();
        //计算结束行数
        setLastIndex();
        //装入结果集
        setResultList(jTemplate.queryForList(getMySQLPageSQL(new StringBuilder(sql), pageRequest), params));
    }


    /**
     * 构造MySQL数据分页SQL
     *
     * @param queryString
     * @return
     */
    public String getMySQLPageSQL(StringBuilder queryString, PageRequest pageRequest) {
        String resultSql = null;
        if (pageRequest != null && pageRequest.getSort() != null && pageRequest.getDir() != null) {
            queryString.append(" order by ").append(pageRequest.getSort()).append(" ").append(pageRequest.getDir());
        }
        if (0 != pageSize) {
            resultSql = queryString.append(" limit ").append(startIndex).append(",").append(pageSize).toString();
        } else {
            resultSql = queryString.toString();
        }
        return resultSql;
    }


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        //如果当前页码<1，则默认加载第一页数据
        if (currentPage < 0) {
            this.currentPage = 1;
        } else {
            this.currentPage = currentPage;
        }
    }


    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public int getTotalPages() {
        return totalPages;
    }

    //计算总页数
    public void setTotalPages() {
        if (pageSize == 0) {
            totalPages = 0;
        } else {
            if (totalRows % pageSize == 0) {
                this.totalPages = totalRows / pageSize;
            } else {
                this.totalPages = (totalRows / pageSize) + 1;
            }
        }
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex() {
        if (currentPage <= 1) {
            startIndex = 0;
        } else {
            startIndex = (currentPage - 1) * getPageSize();
        }

    }

    public int getLastIndex() {
        return lastIndex;
    }

    public JdbcTemplate getJTemplate() {
        return jTemplate;
    }

    public void setJTemplate(JdbcTemplate template) {
        jTemplate = template;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    //计算结束时候的索引
    public void setLastIndex() {

        this.lastIndex = pageSize;
    }

}
