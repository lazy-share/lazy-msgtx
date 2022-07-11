package com.lazy.msgtx.example;

import com.alibaba.fastjson.JSON;
import com.lazy.msgtx.example.dao.OrderDetailRepository;
import com.lazy.msgtx.example.dao.OrderMainRepository;
import com.lazy.msgtx.example.entity.OrderDetail;
import com.lazy.msgtx.example.entity.OrderMain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
public class OrderMainTest extends BaseTest {


    @Autowired
    OrderMainRepository orderMainRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;


    @Test
    public void testCreateOrder() throws Exception {

        OrderMain orderMain = new OrderMain();
        orderMain.setOrderNo("011001");
        orderMain.setCustomerCode("C10");
        orderMain.setCustomerName("张三");
        orderMain.setAddress("广东省深圳市南山区科技园X座5楼");

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(orderDetail.getOrderNo());
        orderDetail.setGoodsQuantity(2);
        orderDetail.setGoodsUnitPrice(BigDecimal.valueOf(100));
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailList.add(orderDetail);

        orderMain.setOrderDetails(orderDetailList);
        orderMain.setTotalAmount(orderDetail.getGoodsUnitPrice().multiply(BigDecimal.valueOf(orderDetail.getGoodsQuantity())));

        System.out.println(JSON.toJSONString(orderMain));
        MvcResult result = this.mockMvc.perform(post("/orderMain/create")
                .content(JSON.toJSONString(orderMain))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        System.out.println("创建订单结果：======================》" + result);
    }

}
