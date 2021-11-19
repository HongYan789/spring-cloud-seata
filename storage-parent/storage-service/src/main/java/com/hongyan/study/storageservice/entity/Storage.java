package com.hongyan.study.storageservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-11-02 09:51
 * @description 库存entity
 */
@Data
public class Storage implements Serializable {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 商品编码
     */
    private String productCode;
    /**
     * 库存数量
     */
    private Integer count;
}
