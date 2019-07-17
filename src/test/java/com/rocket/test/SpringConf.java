package com.rocket.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConf {

    @Bean
    public HelloChina getBean(){
        return new HelloChina();
    }
}
