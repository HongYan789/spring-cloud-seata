package com.hongyan.study.order.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-11-01 14:22
 * @description 订单dto
 */
@Data
public class OrderDto implements Serializable {

    /**
     * 用户编码
     */
    private String userId;

    /**
     * 商品编码
     */
    private String productCode;

    /**
     * 商品数量
     */
    private Integer count;

}
