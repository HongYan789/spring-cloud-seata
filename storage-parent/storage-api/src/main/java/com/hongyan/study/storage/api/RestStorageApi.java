package com.hongyan.study.storage.api;

import com.hongyan.study.storage.api.feign.factory.RestStorageApiFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-10-29 15:30
 * @description 库存feign操作
 */
@FeignClient(value = "storage-service", fallbackFactory = RestStorageApiFallbackFactory.class)
public interface RestStorageApi {

    /**
     * 扣减库存
     * @param productCode 商品编码
     * @param count 待扣减数量
     * @return
     */
    @GetMapping("/storage/deduct")
    void deduct(@RequestParam("productCode") String productCode, @RequestParam("count") Integer count);
}
