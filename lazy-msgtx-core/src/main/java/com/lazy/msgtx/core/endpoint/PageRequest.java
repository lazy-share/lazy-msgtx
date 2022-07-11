package com.lazy.msgtx.core.endpoint;

import com.lazy.msgtx.core.MessageLog;
import lombok.Data;

/**
 * <p>
 *   分页请求
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
@Data
public class PageRequest {

    /**
     * 起始页码
     */
    private int page = 1;

    /**
     * 每页显示条数
     */
    private int size;
    /**
     * 默认为10条
     */
    public static final int PAGE_SIZE = 10;
    /**
     * 排序字段
     */
    private String sort = "last_update_date";
    /**
     * asc or desc
     */
    private String dir = "desc";

    private MessageLog messageLog;


}
