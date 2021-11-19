package com.hongyan.study.storage.api.feign.fallback;

import com.hongyan.study.storage.api.RestStorageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestStorageApiFallback implements RestStorageApi {
    private static Logger logger = LoggerFactory.getLogger(RestStorageApiFallback.class);
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public RestStorageApiFallback() {
    }


    @Override
    public void deduct(String productCode, Integer count) {
        logger.error("productCode :{} count:{} deduct error",productCode,count);
    }
}
