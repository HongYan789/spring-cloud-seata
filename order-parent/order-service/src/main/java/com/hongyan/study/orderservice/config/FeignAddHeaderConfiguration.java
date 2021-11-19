package com.hongyan.study.orderservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author zy
 * <p>在调用feign接口之前,将当前线程的reqId放到请求头里面,串联下游应用<p>
 * @date 2020-06-18
 **/
@Component
@Slf4j
public class FeignAddHeaderConfiguration {
    @Bean
    public FeignHeaderRequestInterceptor basicAuthRequestInterceptor() {
        return new FeignHeaderRequestInterceptor();
    }

    public static class FeignHeaderRequestInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            //处理header信息
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        if(RootContext.KEY_XID.toLowerCase().equals(name)){
                            log.info("global xid add next feign api  name:{},value:{}",name,value);
                            template.header(name, value);
                        }
                    }
                }
            }
        }
    }
}

