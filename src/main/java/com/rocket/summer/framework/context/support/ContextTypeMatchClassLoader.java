package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.core.DecoratingClassLoader;
import com.rocket.summer.framework.core.OverridingClassLoader;
import com.rocket.summer.framework.core.SmartClassLoader;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ContextTypeMatchClassLoader extends DecoratingClassLoader implements SmartClassLoader {

    private static Method findLoadedClassMethod;

    static {
        try {
            findLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] {String.class});
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Invalid [java.lang.ClassLoader] class: no 'findLoadedClass' method defined!");
        }
    }


    /** Cache for byte array per class name */
    private final Map bytesCache = new HashMap();


    public ContextTypeMatchClassLoader(ClassLoader parent) {
        super(parent);
    }

    public boolean isClassReloadable(Class clazz) {
        return (clazz.getClassLoader() instanceof ContextOverridingClassLoader);
    }

    /**
     * ClassLoader to be created for each loaded class.
     * Caches class file content but redefines class for each call.
     */
    private class ContextOverridingClassLoader extends OverridingClassLoader {

        public ContextOverridingClassLoader(ClassLoader parent) {
            super(parent);
        }

        protected boolean isEligibleForOverriding(String className) {
            if (isExcluded(className) || ContextTypeMatchClassLoader.this.isExcluded(className)) {
                return false;
            }
            ReflectionUtils.makeAccessible(findLoadedClassMethod);
            ClassLoader parent = getParent();
            while (parent != null) {
                if (ReflectionUtils.invokeMethod(findLoadedClassMethod, parent, new Object[] {className}) != null) {
                    return false;
                }
                parent = parent.getParent();
            }
            return true;
        }

        protected Class loadClassForOverriding(String name) throws ClassNotFoundException {
            byte[] bytes = (byte[]) bytesCache.get(name);
            if (bytes == null) {
                bytes = loadBytesForClass(name);
                if (bytes != null) {
                    bytesCache.put(name, bytes);
                }
                else {
                    return null;
                }
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
