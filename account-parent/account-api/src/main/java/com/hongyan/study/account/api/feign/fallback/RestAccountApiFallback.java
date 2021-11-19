package com.hongyan.study.account.api.feign.fallback;

import com.hongyan.study.account.api.RestAccountApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;

public class RestAccountApiFallback implements RestAccountApi {
    private static Logger logger = LoggerFactory.getLogger(RestAccountApiFallback.class);
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public RestAccountApiFallback() {
    }


    @Override
    public Boolean debit(String userId, BigDecimal money) {
        logger.error("userId :{} money:{} debit error",userId,money);
        return null;
    }
}
