package com.lazy.msgtx.example.controller;

import com.lazy.msgtx.core.redislock.RedisDLock;
import com.lazy.msgtx.example.dto.OrderCreateDto;
import com.lazy.msgtx.example.entity.OrderMain;
import com.lazy.msgtx.example.service.OrderMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @RedisDLock(resourceIdType = RedisDLock.ResourceIdType.METHOD_PARAM, paramIdx = 0)
    @GetMapping("/redisLock")
    public String redisLock(String lockId) {

        return "ok";
    }

    @RedisDLock(resourceIdType = RedisDLock.ResourceIdType.OBJECT_FIELD, paramIdx = 0, resourceId = "getOrderNo")
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
