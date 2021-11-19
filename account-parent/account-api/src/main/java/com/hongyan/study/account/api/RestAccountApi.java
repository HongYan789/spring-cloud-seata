package com.hongyan.study.account.api;

import com.hongyan.study.account.api.feign.factory.RestAccountApiFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-10-29 15:30
 * @description 账户feign操作
 */
@FeignClient(value = "account-service", fallbackFactory = RestAccountApiFallbackFactory.class)
public interface RestAccountApi {

    /**
     * 从用户账户中借出
     */
    @GetMapping("/account/debit")
    Boolean debit(@RequestParam("userId") String userId,@RequestParam("money") BigDecimal money);
}
