package com.olc.ejdemo;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;

@Slf4j
@SpringBootApplication
@EnableEurekaServer
@MapperScan("com.olc.ejdemo.mapper")
@Configuration("com.olc.ejdemo")
public class EjdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EjdemoApplication.class, args);
    }

}
