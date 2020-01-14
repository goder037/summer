package com.rocket.summer.framework.cglib.beans;

import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.asm.Type;
import com.rocket.summer.framework.cglib.core.AbstractClassGenerator;
import com.rocket.summer.framework.cglib.core.ClassEmitter;
import com.rocket.summer.framework.cglib.core.CodeEmitter;
import com.rocket.summer.framework.cglib.core.Constants;
import com.rocket.summer.framework.cglib.core.Converter;
import com.rocket.summer.framework.cglib.core.EmitUtils;
import com.rocket.summer.framework.cglib.core.KeyFactory;
import com.rocket.summer.framework.cglib.core.Local;
import com.rocket.summer.framework.cglib.core.MethodInfo;
import com.rocket.summer.framework.cglib.core.ReflectUtils;
import com.rocket.summer.framework.cglib.core.Signature;
import com.rocket.summer.framework.cglib.core.TypeUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Nokleberg
 */
abstract public class BeanCopier
{
    private static final BeanCopierKey KEY_FACTORY =
            (BeanCopierKey) KeyFactory.create(BeanCopierKey.class);
    private static final Type CONVERTER =
            TypeUtils.parseType("com.rocket.summer.framework.cglib.core.Converter");
    private static final Type BEAN_COPIER =
            TypeUtils.parseType("com.rocket.summer.framework.cglib.beans.BeanCopier");
    private static final Signature COPY =
            new Signature("copy", Type.VOID_TYPE, new Type[]{ Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER });
    private static final Signature CONVERT =
            TypeUtils.parseSignature("Object convert(Object, Class, Object)");

    interface BeanCopierKey {
        public Object newInstance(String source, String target, boolean useConverter);
    }

    public static BeanCopier create(Class source, Class target, boolean useConverter) {
        Generator gen = new Generator();
        gen.setSource(source);
        gen.setTarget(target);
        gen.setUseConverter(useConverter);
        return gen.create();
    }

    abstract public void copy(Object from, Object to, Converter converter);

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(BeanCopier.class.getName());
        private Class source;
        private Class target;
        private boolean useConverter;

        public Generator() {
            super(SOURCE);
        }

        public void setSource(Class source) {
            if(!Modifier.isPublic(source.getModifiers())){
                setNamePrefix(source.getName());
            }
            this.source = source;
        }

        public void setTarget(Class target) {
            if(!Modifier.isPublic(target.getModifiers())){
                setNamePrefix(target.getName());
            }

            this.target = target;
        }

        public void setUseConverter(boolean useConverter) {
            this.useConverter = useConverter;
        }

        protected ClassLoader getDefaultClassLoader() {
            return source.getClassLoader();
        }

        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(source);
        }

        public BeanCopier create() {
            Object key = KEY_FACTORY.newInstance(source.getName(), target.getName(), useConverter);
            return (BeanCopier)super.create(key);
        }

        public void generateClass(ClassVisitor v) {
            Type sourceType = Type.getType(source);
            Type targetType = Type.getType(target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_2,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    BEAN_COPIER,
                    null,
                    Constants.SOURCE_FILE);

            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);
            PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(source);
            PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(target);

            Map names = new HashMap();
            for (int i = 0; i < getters.length; i++) {
                names.put(getters[i].getName(), getters[i]);
            }
            Local targetLocal = e.make_local();
            Local sourceLocal = e.make_local();
            if (useConverter) {
                e.load_arg(1);
                e.checkcast(targetType);
                e.store_local(targetLocal);
                e.load_arg(0);
                e.checkcast(sourceType);
                e.store_local(sourceLocal);
            } else {
                e.load_arg(1);
                e.checkcast(targetType);
                e.load_arg(0);
                e.checkcast(sourceType);
            }
            for (int i = 0; i < setters.length; i++) {
                PropertyDescriptor setter = setters[i];
                PropertyDescriptor getter = (PropertyDescriptor)names.get(setter.getName());
                if (getter != null) {
                    MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
                    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
                    if (useConverter) {
                        Type setterType = write.getSignature().getArgumentTypes()[0];
                        e.load_local(targetLocal);
                        e.load_arg(2);
                        e.load_local(sourceLocal);
                        e.invoke(read);
                        e.box(read.getSignature().getReturnType());
                        EmitUtils.load_class(e, setterType);
                        e.push(write.getSignature().getName());
                        e.invoke_interface(CONVERTER, CONVERT);
                        e.unbox_or_zero(setterType);
                        e.invoke(write);
                    } else if (compatible(getter, setter)) {
                        e.dup2();
                        e.invoke(read);
                        e.invoke(write);
                    }
                }
            }
            e.return_value();
            e.end_method();
            ce.end_class();
        }

        private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
            // TODO: allow automatic widening conversions?
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
