package com.rocket.test;


import com.rocket.summer.framework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Conf.class);
        ctx.scan("com.rocket.test");
        HelloWorld hw = ctx.getBean(HelloWorld.class);
        hw.getMessage1();
        HelloChina helloChina = ctx.getBean(HelloChina.class);
        System.out.println(helloChina);
        HelloChina helloChina1 = ctx.getBean(HelloChina.class);
        System.out.println(helloChina1);
    }
}
