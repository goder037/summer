package com.rocket.summer.framework.cglib.transform;

import com.rocket.summer.framework.asm.Attribute;
import com.rocket.summer.framework.asm.ClassReader;
import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.cglib.core.ClassGenerator;

public class ClassReaderGenerator implements ClassGenerator {
    private final ClassReader r;
    private final Attribute[] attrs;
    private final int flags;

    public ClassReaderGenerator(ClassReader r, int flags) {
        this(r, null, flags);
    }

    public ClassReaderGenerator(ClassReader r, Attribute[] attrs, int flags) {
        this.r = r;
        this.attrs = (attrs != null) ? attrs : new Attribute[0];
        this.flags = flags;
    }

    public void generateClass(ClassVisitor v) {
        r.accept(v, attrs, flags);
    }
}

