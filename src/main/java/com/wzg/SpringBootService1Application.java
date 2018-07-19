package com.wzg;

import javax.jms.JMSConnectionFactory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.wzg.dao")
public class SpringBootService1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootService1Application.class, args);
	}
}
