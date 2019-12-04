package com.rocket.summer.framework.cglib.proxy;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.cglib.core.ReflectUtils;

import java.lang.reflect.Method;

/**
 * @author Chris Nokleberg
 * @version $Id: MixinBeanEmitter.java,v 1.2 2004/06/24 21:15:20 herbyderby Exp $
 */
class MixinBeanEmitter extends MixinEmitter {
    public MixinBeanEmitter(ClassVisitor v, String className, Class[] classes) {
        super(v, className, classes, null);
    }

    protected Class[] getInterfaces(Class[] classes) {
        return null;
    }

    protected Method[] getMethods(Class type) {
        return ReflectUtils.getPropertyMethods(ReflectUtils.getBeanProperties(type), true, true);
    }
}

