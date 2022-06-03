package com.lazy.msgtx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lazy.msgtx.example.dto.OrderCreateDto;
import com.lazy.msgtx.example.entity.OrderMain;
import com.lazy.msgtx.example.service.OrderMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@RestController
@RequestMapping("orderMain")
public class OrderMainController {

    @Autowired
    OrderMainService orderMainService;


    @PostMapping("/create")
    public String createOrder(@RequestBody OrderMain orderMain) {

        //1、参数校验

        //2、保存订单
        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setOrderMain(orderMain);
        orderMainService.createOrder(createDto);

        return "ok";
    }

}
