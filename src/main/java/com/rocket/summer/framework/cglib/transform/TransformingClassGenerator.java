package com.rocket.summer.framework.cglib.transform;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.cglib.core.ClassGenerator;

public class TransformingClassGenerator implements ClassGenerator {
    private ClassGenerator gen;
    private ClassTransformer t;

    public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t) {
        this.gen = gen;
        this.t = t;
    }

    public void generateClass(ClassVisitor v) throws Exception {
        t.setTarget(v);
        gen.generateClass(t);
    }
}
