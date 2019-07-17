package com.rocket.test;

import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Scope;

@Configuration
public class Conf {

    @Bean
//    @Scope("prototype")
    public HelloChina getBean(){
        return new HelloChina();
    }

}
