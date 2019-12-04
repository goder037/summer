package com.rocket.summer.framework;

import com.rocket.summer.framework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DemoTest {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> classBook = Class.forName("com.rocket.summer.framework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration$EmbeddedTomcat");
        System.out.println(classBook);
        Constructor<?>[] constructors = classBook.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            constructors[i].setAccessible(true);
            Object o = constructors[i].newInstance();
            System.out.println(o);
        }

    }
}
