package com.rocket.summer.framework.cglib.core;


import com.rocket.summer.framework.asm.ClassVisitor;

public interface ClassGenerator {
    void generateClass(ClassVisitor v) throws Exception;
}

