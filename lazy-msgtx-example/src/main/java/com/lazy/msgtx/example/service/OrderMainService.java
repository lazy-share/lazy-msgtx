package com.lazy.msgtx.example.service;

import com.alibaba.fastjson.JSON;
import com.lazy.msgtx.core.MessageTransaction;
import com.lazy.msgtx.example.common.Cost;
import com.lazy.msgtx.example.dao.OrderDetailRepository;
import com.lazy.msgtx.example.dao.OrderMainRepository;
import com.lazy.msgtx.example.dto.LogisticsOrderDto;
import com.lazy.msgtx.example.dto.OrderCreateDto;
import com.lazy.msgtx.example.dto.OrderReport;
import com.lazy.msgtx.example.entity.OrderMain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 *  订单服务
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Slf4j
@Service
public class OrderMainService {

    @Autowired
    OrderMainRepository orderMainRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;


    //消息事务组-根
    @MessageTransaction(messageType = Cost.CREATE_ORDER)
    public void createOrder(OrderCreateDto createDto) {

        //启动类必须配置@EnableAspectJAutoProxy(exposeProxy = true)
        //获取AOP切面代理对象，对当前类方法的调用一定要使用AOP切面代理对象才行
        OrderMainService orderMainService = ((OrderMainService) AopContext.currentProxy());

        //消息事务组-分支子调用-保持订单
        orderMainService.save(createDto);

        //消息事务组-分支子调用-保存订单明细
        orderMainService.saveDetail(createDto);

        //消息事务组-分支子调用-推送物流子系统
        orderMainService.toLgst(createDto);

        //消息事务组-分支子调用-推送积分子系统
        orderMainService.toIngl(createDto);

        //消息事务组-分支子调用-推送报表子系统
        orderMainService.toReport(createDto);

    }

    //消息事务组-分支子调用-保持订单
    @MessageTransaction(messageType = Cost.CREATE_ORDER_SAVE)
    public void save(OrderCreateDto createDto) {

        orderMainRepository.save(createDto.getOrderMain());

        //模拟异常，会回滚当前订单
//        int a = 1 / 0;

        log.info("保存订单成功");
    }

    //消息事务组-分支子调用-保存订单明细
    @MessageTransaction(messageType = Cost.CREATE_ORDER_SAVE_DETAIL)
    public void saveDetail(OrderCreateDto createDto) {

        //模拟计算积分金额
        createDto.setAddIntegral(BigDecimal.valueOf(10));

        //计算报表,给后面方法用
        OrderReport orderReport = new OrderReport();
        orderReport.setOrderNo(createDto.getOrderMain().getOrderNo());
        orderReport.setAmount(createDto.getOrderMain().getTotalAmount());
        orderReport.setCustomerCode(createDto.getOrderMain().getCustomerCode());
        createDto.setOrderReport(orderReport);

        orderDetailRepository.saveAll(createDto.getOrderMain().getOrderDetails());

        //模拟异常，会回滚当前订单明细，但是不会回滚上面的订单
        //可以通过将这里代码放进上面创建订单方法就可以保存要么一起成功，要么一起失败，而不是独立子调用
//        int a = 1 / 0;
        log.info("save detail success");

    }

    //消息事务组-分支子调用-推送物流子系统
    @MessageTransaction(messageType = Cost.CREATE_ORDER_TO_LGST)
    public void toLgst(OrderCreateDto createDto) {

        //模拟创建物流需要的数据
        OrderMain orderMain = createDto.getOrderMain();
        LogisticsOrderDto lgstOrder = new LogisticsOrderDto();
        lgstOrder.setAddress(orderMain.getAddress());
        lgstOrder.setMobile("13222222222");
        lgstOrder.setCustomerName(orderMain.getCustomerName());
        createDto.setLogisticsOrderDto(lgstOrder);

        //模拟异常，只会回滚该子调用，前面的子调用不会回滚
//        int a = 1 / 0;

        log.info("推送物流单据：{}", JSON.toJSONString(lgstOrder));
    }

    //消息事务组-分支子调用-推送积分子系统
    @MessageTransaction(messageType = Cost.CREATE_ORDER_TO_INGL)
    public void toIngl(OrderCreateDto createDto) {

        //模拟异常，只会回滚该子调用，前面的子调用不会回滚
//        int a = 1 / 0;
        log.info("推送积分：" + createDto.getAddIntegral());
    }

    //消息事务组-分支子调用-推送报表子系统
    @MessageTransaction(messageType = Cost.CREATE_ORDER_TO_REPORT)
    public void toReport(OrderCreateDto createDto) {

        //模拟异常，只会回滚该子调用，前面的子调用不会回滚
//        int a = 1 / 0;
        log.info("推送报表：{}", JSON.toJSONString(createDto.getOrderReport()));
    }

}
