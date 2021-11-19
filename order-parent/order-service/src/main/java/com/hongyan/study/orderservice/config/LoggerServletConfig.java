package com.hongyan.study.orderservice.config;

import com.hongyan.study.orderservice.filter.SeataFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerServletConfig {
    @Bean
    @ConditionalOnMissingBean(name = {"loggerServletFilter"})
    public SeataFilter loggerServletFilter() {
        return new SeataFilter();
    }
}
