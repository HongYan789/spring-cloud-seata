package com.hongyan.study.customerservice.controller;

import com.alibaba.fastjson.JSON;
import com.hongyan.study.account.api.RestAccountApi;
import com.hongyan.study.order.api.RestOrderApi;
import com.hongyan.study.order.api.model.OrderDto;
import com.hongyan.study.storage.api.RestStorageApi;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-11-01 15:32
 * @description 订单处理服务
 */
@Slf4j
@RestController
@RequestMapping("/customer/order")
public class OrderController {

    @Autowired
    private RestAccountApi restAccountApi;

    @Autowired
    private RestOrderApi restOrderApi;

    @Autowired
    private RestStorageApi restStorageApi;

    @GlobalTransactional(name = "spring-cloud-seata-tx")
    @GetMapping("/createOrder")
    public String createOrder(String userId, String productCode, Integer count, BigDecimal money,Boolean exception){
        log.info("<createOrder-req> userId:{}, productCode:{},count:{},money:{},exception:{}",userId,productCode,count,money,exception);
        log.info("business Service Begin ... xid: {}",RootContext.getXID());
        OrderDto orderDto = restOrderApi.create(userId, productCode, count);
        Boolean flag = restAccountApi.debit(userId,money);
        //模拟异常情况
        if(exception){
            int i = 1/0;
        }

        restStorageApi.deduct(productCode, count);

        log.info("<createOrder-resp> orderDto:{},flag:{}", JSON.toJSONString(orderDto),flag);

        return "success";
    }
}
