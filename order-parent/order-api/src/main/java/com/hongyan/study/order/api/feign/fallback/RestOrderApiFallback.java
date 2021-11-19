package com.hongyan.study.order.api.feign.fallback;

import com.hongyan.study.order.api.RestOrderApi;
import com.hongyan.study.order.api.model.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestOrderApiFallback implements RestOrderApi {
    private static Logger logger = LoggerFactory.getLogger(RestOrderApiFallback.class);
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public RestOrderApiFallback() {
    }


    @Override
    public OrderDto create(String userId, String productCode, Integer count) {
        logger.error("userId :{}, productCode:{}, count:{} create error",userId,productCode,count);
        return null;
    }
}
