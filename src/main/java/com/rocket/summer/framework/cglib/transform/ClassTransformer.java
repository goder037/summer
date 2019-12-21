package com.rocket.summer.framework.cglib.transform;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.cglib.core.Constants;

public abstract class ClassTransformer extends ClassVisitor {
    public ClassTransformer() {
        super(Constants.ASM_API);
    }
    public ClassTransformer(int opcode) {
        super(opcode);
    }
    public abstract void setTarget(ClassVisitor target);
}
