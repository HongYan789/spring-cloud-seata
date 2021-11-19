package com.hongyan.study.order.api.feign.factory;

import com.hongyan.study.order.api.RestOrderApi;
import com.hongyan.study.order.api.feign.fallback.RestOrderApiFallback;
import feign.hystrix.FallbackFactory;

public class RestOrderApiFallbackFactory implements FallbackFactory<RestOrderApi> {


    @Override
    public RestOrderApi create(Throwable e) {
        RestOrderApiFallback restServiceFallback = new RestOrderApiFallback();
        restServiceFallback.setCause(e);
        return restServiceFallback;
    }
}
