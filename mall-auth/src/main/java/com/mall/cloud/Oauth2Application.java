package com.mall.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//使用EnableDiscoveryClient替代 Eureka的注解 @EnableEurekaClient
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class Oauth2Application {
    public static void main(String[] args) {
        SpringApplication.run(Oauth2Application.class, args);
    }


    @Bean(name = "restTemplate")
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
