package com.hongyan.study.accountservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.hongyan.study.account","com.hongyan.study.accountservice"}) //启用feign，并指定扫描路径
@MapperScan("com.hongyan.study.accountservice.mapper") //提供mybatisplus文件扫描，添加注解后则mapper文件无需添加@Mapper注解
@EnableTransactionManagement //开启本地事务
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

}
