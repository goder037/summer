package com.rocket.summer.framework.cglib.core;

public interface GeneratorStrategy {
    byte[] generate(ClassGenerator var1) throws Exception;

    boolean equals(Object var1);
}
