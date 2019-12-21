package com.rocket.summer.framework.cglib.proxy;

import com.rocket.summer.framework.cglib.core.ClassEmitter;
import com.rocket.summer.framework.cglib.core.CodeEmitter;
import com.rocket.summer.framework.cglib.core.EmitUtils;
import com.rocket.summer.framework.cglib.core.MethodInfo;
import com.rocket.summer.framework.cglib.core.TypeUtils;

import java.util.Iterator;
import java.util.List;

class NoOpGenerator
        implements CallbackGenerator
{
    public static final NoOpGenerator INSTANCE = new NoOpGenerator();

    public void generate(ClassEmitter ce, Context context, List methods) {
        for (Iterator it = methods.iterator(); it.hasNext();) {
            MethodInfo method = (MethodInfo)it.next();
            if (TypeUtils.isBridge(method.getModifiers()) || (
                    TypeUtils.isProtected(context.getOriginalModifiers(method)) &&
                            TypeUtils.isPublic(method.getModifiers()))) {
                CodeEmitter e = EmitUtils.begin_method(ce, method);
                e.load_this();
                context.emitLoadArgsAndInvoke(e, method);
                e.return_value();
                e.end_method();
            }
        }
    }

    public void generateStatic(CodeEmitter e, Context context, List methods) { }
}

