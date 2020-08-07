package com.olc.ejdemo.config;

import com.olc.ejdemo.filter.WebLogAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @Description
 * @Author ex-oulingcheng
 * @Date 2020/8/7 15:46
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.olc.ejdemo.web")
public class SpringMvcAutoConfigure {
    @Bean
    @ConditionalOnMissingBean(WebLogAspect.class)
    public WebLogAspect webLogAspect() {
        return new WebLogAspect();
    }
}
