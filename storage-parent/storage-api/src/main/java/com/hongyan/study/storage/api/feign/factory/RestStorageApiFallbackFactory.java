package com.hongyan.study.storage.api.feign.factory;

import com.hongyan.study.storage.api.RestStorageApi;
import com.hongyan.study.storage.api.feign.fallback.RestStorageApiFallback;
import feign.hystrix.FallbackFactory;

public class RestStorageApiFallbackFactory implements FallbackFactory<RestStorageApi> {


    @Override
    public RestStorageApi create(Throwable e) {
        RestStorageApiFallback restServiceFallback = new RestStorageApiFallback();
        restServiceFallback.setCause(e);
        return restServiceFallback;
    }
}
