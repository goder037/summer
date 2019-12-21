package com.rocket.summer.framework.cglib.transform;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.cglib.core.Constants;

abstract public class AbstractClassTransformer extends ClassTransformer {
    protected AbstractClassTransformer() {
        super(Constants.ASM_API);
    }

    public void setTarget(ClassVisitor target) {
        cv = target;
    }
}
