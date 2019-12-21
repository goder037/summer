package com.rocket.summer.framework.cglib.proxy;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.cglib.core.CollectionUtils;
import com.rocket.summer.framework.cglib.core.ReflectUtils;
import com.rocket.summer.framework.cglib.core.RejectModifierPredicate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Chris Nokleberg
 * @version $Id: MixinEverythingEmitter.java,v 1.3 2004/06/24 21:15:19 herbyderby Exp $
 */
class MixinEverythingEmitter extends MixinEmitter {

    public MixinEverythingEmitter(ClassVisitor v, String className, Class[] classes) {
        super(v, className, classes, null);
    }

    protected Class[] getInterfaces(Class[] classes) {
        List list = new ArrayList();
        for (int i = 0; i < classes.length; i++) {
            ReflectUtils.addAllInterfaces(classes[i], list);
        }
        return (Class[])list.toArray(new Class[list.size()]);
    }

    protected Method[] getMethods(Class type) {
        List methods = new ArrayList(Arrays.asList(type.getMethods()));
        CollectionUtils.filter(methods, new RejectModifierPredicate(Modifier.FINAL | Modifier.STATIC));
        return (Method[])methods.toArray(new Method[methods.size()]);
    }
}

