package com.hongyan.study.orderservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-11-01 18:12
 * @description 订单entity
 * 很神奇的现象，采用order表时启动就报错，只能修改表名再实现
 */
@Data
@TableName("tmp_order")
public class Order implements Serializable {
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
     * 商品编码
     */
    private String productCode;
    /**
     * 商品购买数量
     */
    private Integer count;
    /**
     * 商品金额
     */
    private BigDecimal money;
}
