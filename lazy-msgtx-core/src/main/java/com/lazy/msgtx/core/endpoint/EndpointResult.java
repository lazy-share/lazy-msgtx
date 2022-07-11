package com.lazy.msgtx.core.endpoint;

import com.lazy.msgtx.core.common.Const;
import lombok.Data;

/**
 * <p>
 * 端点执行结果
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
@Data
public class EndpointResult {

    private String code;
    private String message;
    private Object data;

    public static EndpointResult success(Object data) {
        EndpointResult result = new EndpointResult();
        result.setCode(String.valueOf(Const.ONE));
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static EndpointResult fail(String failMsg) {
        EndpointResult result = new EndpointResult();
        result.setCode(String.valueOf(Const.ZERO));
        result.setMessage(failMsg);
        result.setData(null);
        return result;
    }

}
