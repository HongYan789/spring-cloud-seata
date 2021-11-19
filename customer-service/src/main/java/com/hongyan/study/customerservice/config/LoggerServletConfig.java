package com.hongyan.study.customerservice.config;

import com.hongyan.study.customerservice.filter.SeataFilter;
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
