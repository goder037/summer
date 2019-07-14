package com.rocket.test;


import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        HelloWorld obj = (HelloWorld) context.getBean("helloWorld");
        obj.getMessage1();

        HelloChina objB = (HelloChina) context.getBean("helloChina");
        objB.getMessage1();
        objB.getMessage2();
        objB.getMessage3();
    }
}
