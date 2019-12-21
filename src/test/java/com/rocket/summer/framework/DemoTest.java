package com.rocket.summer.framework;

import com.rocket.summer.framework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

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

    @Test
    public void testMain(){
        Hashtable<String, String> table = new Hashtable<>();
        table.put("test", "tt");
        table.put("123", "123");
        Enumeration<String> elements = table.elements();
        System.out.println(elements);
        System.out.println(table.contains("123"));
        HashMap<String, String> map = new HashMap<>();
        map.put("tt", "123");
        map.put("123", "yy");
    }
}
