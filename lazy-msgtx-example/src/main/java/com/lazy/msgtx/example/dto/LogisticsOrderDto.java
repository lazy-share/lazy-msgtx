package com.lazy.msgtx.example.dto;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Data
public class LogisticsOrderDto {

    //收货地址
    private String address;
    //手机号码
    private String mobile;
    //客户名称
    private String customerName;

}
