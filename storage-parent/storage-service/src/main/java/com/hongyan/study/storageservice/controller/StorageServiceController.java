package com.hongyan.study.storageservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hongyan.study.storage.api.RestStorageApi;
import com.hongyan.study.storageservice.entity.Storage;
import com.hongyan.study.storageservice.mapper.StorageMapper;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2021-11-01 15:09
 * @description 库存核心入口服务
 */
@Slf4j
@RestController
@RequestMapping("/storage")
public class StorageServiceController implements RestStorageApi{

    @Autowired
    private StorageMapper storageMapper;
    /**
     *  扣减库存功能
     * @param productCode 商品编码
     * @param count 待扣减数量
     */
    @RequestMapping("/deduct")
    @Override
    public void deduct(String productCode, Integer count) {
        log.info("<deduct-req> productCode:{},count:{}",productCode,count);
        log.info("storage Service Begin ... xid: {}", RootContext.getXID());

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("product_code",productCode);
        Storage storage =  storageMapper.selectOne(queryWrapper);
        if(storage == null){
            log.error("不存在该库存商品");
            return;
        }
        if(storage.getCount() < count){
            log.error("库存不足");
            return;
        }
        UpdateWrapper updateWrapper =  new UpdateWrapper();
        updateWrapper.eq("product_code",productCode);
        storage.setCount(storage.getCount()-count);
        int result = storageMapper.update(storage,updateWrapper);
//        int result = storageMapper.updateById(storage);
        log.info("deduct success!!!,resultSize:{}",result);
    }
}
