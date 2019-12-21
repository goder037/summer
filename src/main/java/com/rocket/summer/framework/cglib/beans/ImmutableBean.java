package com.rocket.summer.framework.cglib.beans;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.asm.Type;
import com.rocket.summer.framework.cglib.core.AbstractClassGenerator;
import com.rocket.summer.framework.cglib.core.ClassEmitter;
import com.rocket.summer.framework.cglib.core.CodeEmitter;
import com.rocket.summer.framework.cglib.core.Constants;
import com.rocket.summer.framework.cglib.core.EmitUtils;
import com.rocket.summer.framework.cglib.core.MethodInfo;
import com.rocket.summer.framework.cglib.core.ReflectUtils;
import com.rocket.summer.framework.cglib.core.Signature;
import com.rocket.summer.framework.cglib.core.TypeUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

/**
 * @author Chris Nokleberg
 */
public class ImmutableBean
{
    private static final Type ILLEGAL_STATE_EXCEPTION =
            TypeUtils.parseType("IllegalStateException");
    private static final Signature CSTRUCT_OBJECT =
            TypeUtils.parseConstructor("Object");
    private static final Class[] OBJECT_CLASSES = { Object.class };
    private static final String FIELD_NAME = "CGLIB$RWBean";

    private ImmutableBean() {
    }

    public static Object create(Object bean) {
        Generator gen = new Generator();
        gen.setBean(bean);
        return gen.create();
    }

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(ImmutableBean.class.getName());
        private Object bean;
        private Class target;

        public Generator() {
            super(SOURCE);
        }

        public void setBean(Object bean) {
            this.bean = bean;
            target = bean.getClass();
        }

        protected ClassLoader getDefaultClassLoader() {
            return target.getClassLoader();
        }

        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(target);
        }

        public Object create() {
            String name = target.getName();
            setNamePrefix(name);
            return super.create(name);
        }

        public void generateClass(ClassVisitor v) {
            Type targetType = Type.getType(target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_2,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    targetType,
                    null,
                    Constants.SOURCE_FILE);

            ce.declare_field(Constants.ACC_FINAL | Constants.ACC_PRIVATE, FIELD_NAME, targetType, null);

            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, CSTRUCT_OBJECT, null);
            e.load_this();
            e.super_invoke_constructor();
            e.load_this();
            e.load_arg(0);
            e.checkcast(targetType);
            e.putfield(FIELD_NAME);
            e.return_value();
            e.end_method();

            PropertyDescriptor[] descriptors = ReflectUtils.getBeanProperties(target);
            Method[] getters = ReflectUtils.getPropertyMethods(descriptors, true, false);
            Method[] setters = ReflectUtils.getPropertyMethods(descriptors, false, true);

            for (int i = 0; i < getters.length; i++) {
                MethodInfo getter = ReflectUtils.getMethodInfo(getters[i]);
                e = EmitUtils.begin_method(ce, getter, Constants.ACC_PUBLIC);
                e.load_this();
                e.getfield(FIELD_NAME);
                e.invoke(getter);
                e.return_value();
                e.end_method();
            }

            for (int i = 0; i < setters.length; i++) {
                MethodInfo setter = ReflectUtils.getMethodInfo(setters[i]);
                e = EmitUtils.begin_method(ce, setter, Constants.ACC_PUBLIC);
                e.throw_exception(ILLEGAL_STATE_EXCEPTION, "Bean is immutable");
                e.end_method();
            }

            ce.end_class();
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type, OBJECT_CLASSES, new Object[]{ bean });
        }

        // TODO: optimize
        protected Object nextInstance(Object instance) {
            return firstInstance(instance.getClass());
        }
    }
}

