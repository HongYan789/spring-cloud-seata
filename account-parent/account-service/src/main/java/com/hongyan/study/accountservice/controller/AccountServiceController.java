package com.hongyan.study.accountservice.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hongyan.study.account.api.RestAccountApi;
import com.hongyan.study.accountservice.entity.Account;
import com.hongyan.study.accountservice.mapper.AccountMapper;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-10-29 15:38
 * @description 账户信息controller
 */
@RestController
@RequestMapping("/account")
@Slf4j
public class AccountServiceController implements RestAccountApi {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 模拟从用户账户借支
     * @param userId
     * @param money
     * @return
     */
    @Override
    @GetMapping("/debit")
    public Boolean debit(String userId, BigDecimal money) {
        log.info("<debit-req> userId:{},money:{}",userId,money);
        log.info("account Service Begin ... xid: {}", RootContext.getXID());

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        Account account = accountMapper.selectOne(queryWrapper);
        if(account == null){
            log.error("不存在的账户");
            return false;
        }
        if(account.getMoney().compareTo(money) < 0){
            log.error("账户余额不足");
            return false;
        }
//        UpdateWrapper updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("user_id",userId);
        BigDecimal currentMoney = account.getMoney().subtract(money);
        account.setMoney(currentMoney);
        int result = accountMapper.updateById(account);
        log.info("<debit-resp> account:{},result:{}", JSON.toJSONString(account),result);
        return result > 0;
    }
}
