package com.hongyan.study.orderservice.controller;

import com.alibaba.fastjson.JSON;
import com.hongyan.study.order.api.RestOrderApi;
import com.hongyan.study.order.api.model.OrderDto;
import com.hongyan.study.orderservice.entity.Order;
import com.hongyan.study.orderservice.mapper.OrderMapper;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-11-01 14:49
 * @description 订单服务请求入口
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderServiceController implements RestOrderApi {

    @Autowired
    private OrderMapper orderMapper;
    /*
     * 创建订单
     * @author zy
     * @date 2021-11-01 14:50
     * @description 创建订单
     * @param [userId, productCode, count]
     * @return com.hongyan.study.order.api.model.OrderDto
     */
    @GetMapping("/create")
    @Override
    public OrderDto create(String userId, String productCode, Integer count) {
        log.info("<create-req> userId:{},productCode:{},count:{}",userId,productCode,count);
        log.info("order Service Begin ... xid: {}", RootContext.getXID());

        Order order = new Order();
        order.setUserId(userId);
        order.setProductCode(productCode);
        order.setCount(count);
        int result = orderMapper.insert(order);
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(order,orderDto);
        log.info("<create-resp> orderDto:{},resultCount:{}", JSON.toJSONString(orderDto),result);
        return orderDto;
    }
}
