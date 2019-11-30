package com.rocket.summer.framework.cglib.transform;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.asm.MethodVisitor;

public class MethodFilterTransformer extends AbstractClassTransformer {
    private MethodFilter filter;
    private ClassTransformer pass;
    private ClassVisitor direct;

    public MethodFilterTransformer(MethodFilter filter, ClassTransformer pass) {
        this.filter = filter;
        this.pass = pass;
        super.setTarget(pass);
    }

    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        return (filter.accept(access, name, desc, signature, exceptions) ? pass : direct).visitMethod(access, name, desc, signature, exceptions);
    }

    public void setTarget(ClassVisitor target) {
        pass.setTarget(target);
        direct = target;
    }
}

