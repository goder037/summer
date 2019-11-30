package com.rocket.summer.framework.cglib.proxy;

import com.rocket.summer.framework.asm.Label;
import com.rocket.summer.framework.asm.Type;
import com.rocket.summer.framework.cglib.core.ClassEmitter;
import com.rocket.summer.framework.cglib.core.CodeEmitter;
import com.rocket.summer.framework.cglib.core.Constants;
import com.rocket.summer.framework.cglib.core.MethodInfo;
import com.rocket.summer.framework.cglib.core.Signature;
import com.rocket.summer.framework.cglib.core.TypeUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class LazyLoaderGenerator implements CallbackGenerator {
    public static final LazyLoaderGenerator INSTANCE = new LazyLoaderGenerator();

    private static final Signature LOAD_OBJECT =
            TypeUtils.parseSignature("Object loadObject()");
    private static final Type LAZY_LOADER =
            TypeUtils.parseType("com.rocket.summer.framework.cglib.proxy.LazyLoader");

    public void generate(ClassEmitter ce, Context context, List methods) {
        Set indexes = new HashSet();
        for (Iterator it = methods.iterator(); it.hasNext();) {
            MethodInfo method = (MethodInfo)it.next();
            if (TypeUtils.isProtected(method.getModifiers())) {
                // ignore protected methods
            } else {
                int index = context.getIndex(method);
                indexes.add(new Integer(index));
                CodeEmitter e = context.beginMethod(ce, method);
                e.load_this();
                e.dup();
                e.invoke_virtual_this(loadMethod(index));
                e.checkcast(method.getClassInfo().getType());
                e.load_args();
                e.invoke(method);
                e.return_value();
                e.end_method();
            }
        }

        for (Iterator it = indexes.iterator(); it.hasNext();) {
            int index = ((Integer)it.next()).intValue();

            String delegate = "CGLIB$LAZY_LOADER_" + index;
            ce.declare_field(Constants.ACC_PRIVATE, delegate, Constants.TYPE_OBJECT, null);

            CodeEmitter e = ce.begin_method(Constants.ACC_PRIVATE |
                            Constants.ACC_SYNCHRONIZED |
                            Constants.ACC_FINAL,
                    loadMethod(index),
                    null);
            e.load_this();
            e.getfield(delegate);
            e.dup();
            Label end = e.make_label();
            e.ifnonnull(end);
            e.pop();
            e.load_this();
            context.emitCallback(e, index);
            e.invoke_interface(LAZY_LOADER, LOAD_OBJECT);
            e.dup_x1();
            e.putfield(delegate);
            e.mark(end);
            e.return_value();
            e.end_method();

        }
    }

    private Signature loadMethod(int index) {
        return new Signature("CGLIB$LOAD_PRIVATE_" + index,
                Constants.TYPE_OBJECT,
                Constants.TYPES_EMPTY);
    }

    public void generateStatic(CodeEmitter e, Context context, List methods) { }
}

