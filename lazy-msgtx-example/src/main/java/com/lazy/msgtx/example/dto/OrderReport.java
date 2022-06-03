package com.lazy.msgtx.example.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Data
public class OrderReport {


    private String orderNo;
    private BigDecimal amount;
    private String customerCode;

}
