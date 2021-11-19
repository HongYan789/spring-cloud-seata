package com.hongyan.study.order.api;

import com.hongyan.study.order.api.feign.factory.RestOrderApiFallbackFactory;
import com.hongyan.study.order.api.model.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-10-29 15:30
 * @description 订单feign操作
 */
@FeignClient(value = "order-service", fallbackFactory = RestOrderApiFallbackFactory.class)
public interface RestOrderApi {

    /**
     * 创建订单
     * @param userId 用户编码
     * @param productCode 商品编码
     * @param count 商品数量
     * @return
     */
    @GetMapping("/order/create")
    OrderDto create(@RequestParam("userId") String userId, @RequestParam("productCode") String productCode, @RequestParam("count") Integer count);
}
