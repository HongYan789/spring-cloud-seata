package com.hongyan.study.accountservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-11-01 16:54
 * @description 账户信息
 */
@Data
@TableName("account")
public class Account {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 账户余额
     */
    private BigDecimal money;
}
