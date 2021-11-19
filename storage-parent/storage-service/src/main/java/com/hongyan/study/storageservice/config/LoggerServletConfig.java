package com.hongyan.study.storageservice.config;

import com.hongyan.study.storageservice.filter.SeataFilter;
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
