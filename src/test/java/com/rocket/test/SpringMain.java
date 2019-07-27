package com.rocket.test;

import com.rocket.summer.framework.context.annotation.AnnotationConfigApplicationContext;

public class SpringMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConf.class);
        ctx.scan("com.rocket.test");
        SpringHelloWorld hw = ctx.getBean(SpringHelloWorld.class);
        hw.getMessage1();
        HelloChina helloChina = ctx.getBean(HelloChina.class);
        helloChina.getMessage1();
    }
}
