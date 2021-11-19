package com.hongyan.study.account.api.feign.factory;

import com.hongyan.study.account.api.RestAccountApi;
import com.hongyan.study.account.api.feign.fallback.RestAccountApiFallback;
import feign.hystrix.FallbackFactory;

public class RestAccountApiFallbackFactory implements FallbackFactory<RestAccountApi> {


    @Override
    public RestAccountApi create(Throwable e) {
        RestAccountApiFallback restServiceFallback = new RestAccountApiFallback();
        restServiceFallback.setCause(e);
        return restServiceFallback;
    }
}
