package com.rocket.test;

import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;

@Configuration
public class SpringConf {

    @Bean
    public HelloChina getBean(){
        return new HelloChina();
    }
}
