package com.rocket.summer.framework.cglib.proxy;

import com.rocket.summer.framework.asm.Type;
import com.rocket.summer.framework.cglib.core.ClassEmitter;
import com.rocket.summer.framework.cglib.core.CodeEmitter;
import com.rocket.summer.framework.cglib.core.MethodInfo;
import com.rocket.summer.framework.cglib.core.Signature;
import com.rocket.summer.framework.cglib.core.TypeUtils;

import java.util.Iterator;
import java.util.List;

class FixedValueGenerator implements CallbackGenerator {
    public static final FixedValueGenerator INSTANCE = new FixedValueGenerator();
    private static final Type FIXED_VALUE =
            TypeUtils.parseType("com.rocket.summer.framework.cglib.proxy.FixedValue");
    private static final Signature LOAD_OBJECT =
            TypeUtils.parseSignature("Object loadObject()");

    public void generate(ClassEmitter ce, Context context, List methods) {
        for (Iterator it = methods.iterator(); it.hasNext();) {
            MethodInfo method = (MethodInfo)it.next();
            CodeEmitter e = context.beginMethod(ce, method);
            context.emitCallback(e, context.getIndex(method));
            e.invoke_interface(FIXED_VALUE, LOAD_OBJECT);
            e.unbox_or_zero(e.getReturnType());
            e.return_value();
            e.end_method();
        }
    }

    public void generateStatic(CodeEmitter e, Context context, List methods) { }
}

