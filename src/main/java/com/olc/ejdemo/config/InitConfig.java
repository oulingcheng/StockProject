package com.olc.ejdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Oulingcheng
 * @Date 2020/7/4
 */
@Slf4j
@Component
public class InitConfig implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("初始化elastic-job.xml");
        new ClassPathXmlApplicationContext("applicationContext-elasticjob.xml");
    }
}
