package com.lazy.msgtx.example.dto;

import com.alibaba.fastjson.JSON;
import com.lazy.msgtx.core.provide.MessageProvide;
import com.lazy.msgtx.example.common.Cost;
import com.lazy.msgtx.example.entity.OrderMain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 创建订单DTO
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Data
public class OrderCreateDto extends MessageProvide {

    //订单参数
    private OrderMain orderMain;

    //本次订单需要增加的积分数据
    private BigDecimal addIntegral;

    //本次订单生成的物流单据数据
    private LogisticsOrderDto logisticsOrderDto;

    //本次订单生成的报表推送数据
    private OrderReport orderReport;

    @Override
    public String messageId() {
        return orderMain.getOrderNo();
    }


    @Override
    public String bizId() {
        return orderMain.getOrderNo();
    }
}
