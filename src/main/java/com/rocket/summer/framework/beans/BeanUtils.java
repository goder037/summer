package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.*;

public abstract class BeanUtils {

    private static final Log logger = LogFactory.getLog(BeanUtils.class);

    private static final Map unknownEditorTypes = Collections.synchronizedMap(new WeakHashMap());

    /**
     * Convenience method to instantiate a class using its no-arg constructor.
     * As this method doesn't try to load classes by name, it should avoid
     * class-loading issues.
     * <p>Note that this method tries to set the constructor accessible
     * if given a non-accessible (that is, non-public) constructor.
     * @param clazz class to instantiate
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     */
    public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        }
        catch (NoSuchMethodException ex) {
            throw new BeanInstantiationException(clazz, "No default constructor found", ex);
        }
    }

    /**
     * Convenience method to instantiate a class using the given constructor.
     * As this method doesn't try to load classes by name, it should avoid
     * class-loading issues.
     * <p>Note that this method tries to set the constructor accessible
     * if given a non-accessible (that is, non-public) constructor.
     * @param ctor the constructor to instantiate
     * @param args the constructor arguments to apply
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            ReflectionUtils.makeAccessible(ctor);
            return ctor.newInstance(args);
        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Is the constructor accessible?", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Illegal arguments for constructor", ex);
        }
        catch (InvocationTargetException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Constructor threw exception", ex.getTargetException());
        }
    }

    /**
     * Find a method with the given method name and the given parameter types,
     * declared on the given class or one of its superclasses. Prefers public methods,
     * but will return a protected, package access, or private method too.
     * <p>Checks <code>Class.getMethod</code> first, falling back to
     * <code>findDeclaredMethod</code>. This allows to find public methods
     * without issues even in environments with restricted Java security settings.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @param paramTypes the parameter types of the method to find
     * @return the Method object, or <code>null</code> if not found
     * @see java.lang.Class#getMethod
     * @see #findDeclaredMethod
     */
    public static Method findMethod(Class clazz, String methodName, Class[] paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    /**
     * Find a method with the given method name and the given parameter types,
     * declared on the given class or one of its superclasses. Will return a public,
     * protected, package access, or private method.
     * <p>Checks <code>Class.getDeclaredMethod</code>, cascading upwards to all superclasses.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @param paramTypes the parameter types of the method to find
     * @return the Method object, or <code>null</code> if not found
     * @see java.lang.Class#getDeclaredMethod
     */
    public static Method findDeclaredMethod(Class clazz, String methodName, Class[] paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    /**
     * Find a JavaBeans PropertyEditor following the 'Editor' suffix convention
     * (e.g. "mypackage.MyDomainClass" -> "mypackage.MyDomainClassEditor").
     * <p>Compatible to the standard JavaBeans convention as implemented by
     * {@link java.beans.PropertyEditorManager} but isolated from the latter's
     * registered default editors for primitive types.
     * @param targetType the type to find an editor for
     * @return the corresponding editor, or <code>null</code> if none found
     */
    public static PropertyEditor findEditorByConvention(Class targetType) {
        if (targetType == null || targetType.isArray() || unknownEditorTypes.containsKey(targetType)) {
            return null;
        }
        ClassLoader cl = targetType.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
            if (cl == null) {
                return null;
            }
        }
        String editorName = targetType.getName() + "Editor";
        try {
            Class editorClass = cl.loadClass(editorName);
            if (!PropertyEditor.class.isAssignableFrom(editorClass)) {
                logger.warn("Editor class [" + editorName +
                        "] does not implement [java.beans.PropertyEditor] interface");
                unknownEditorTypes.put(targetType, Boolean.TRUE);
                return null;
            }
            return (PropertyEditor) instantiateClass(editorClass);
        }
        catch (ClassNotFoundException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("No property editor [" + editorName + "] found for type " +
                        targetType.getName() + " according to 'Editor' suffix convention");
            }
            unknownEditorTypes.put(targetType, Boolean.TRUE);
            return null;
        }
    }

    /**
     * Obtain a new MethodParameter object for the write method of the
     * specified property.
     * @param pd the PropertyDescriptor for the property
     * @return a corresponding MethodParameter object
     */
    public static MethodParameter getWriteMethodParameter(PropertyDescriptor pd) {
        if (pd instanceof GenericTypeAwarePropertyDescriptor) {
            return new MethodParameter(
                    ((GenericTypeAwarePropertyDescriptor) pd).getWriteMethodParameter());
        }
        else {
            return new MethodParameter(pd.getWriteMethod(), 0);
        }
    }

    /**
     * Retrieve the JavaBeans <code>PropertyDescriptor</code>s of a given class.
     * @param clazz the Class to retrieve the PropertyDescriptors for
     * @return an array of <code>PropertyDescriptors</code> for the given class
     * @throws BeansException if PropertyDescriptor look fails
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) throws BeansException {
        CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
        return cr.getBeanInfo().getPropertyDescriptors();
    }

    /**
     * Find a method with the given method name and minimal parameters (best case: none),
     * declared on the given class or one of its superclasses. Prefers public methods,
     * but will return a protected, package access, or private method too.
     * <p>Checks <code>Class.getMethods</code> first, falling back to
     * <code>findDeclaredMethodWithMinimalParameters</code>. This allows to find public
     * methods without issues even in environments with restricted Java security settings.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @return the Method object, or <code>null</code> if not found
     * @throws IllegalArgumentException if methods of the given name were found but
     * could not be resolved to a unique method with minimal parameters
     * @see java.lang.Class#getMethods
     * @see #findDeclaredMethodWithMinimalParameters
     */
    public static Method findMethodWithMinimalParameters(Class clazz, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = doFindMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null) {
            return findDeclaredMethodWithMinimalParameters(clazz, methodName);
        }
        return targetMethod;
    }

    /**
     * Find a method with the given method name and minimal parameters (best case: none)
     * in the given list of methods.
     * @param methods the methods to check
     * @param methodName the name of the method to find
     * @return the Method object, or <code>null</code> if not found
     * @throws IllegalArgumentException if methods of the given name were found but
     * could not be resolved to a unique method with minimal parameters
     */
    private static Method doFindMethodWithMinimalParameters(Method[] methods, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                int numParams = methods[i].getParameterTypes().length;
                if (targetMethod == null ||
                        numParams < targetMethod.getParameterTypes().length) {
                    targetMethod = methods[i];
                    numMethodsFoundWithCurrentMinimumArgs = 1;
                }
                else {
                    if (targetMethod.getParameterTypes().length == numParams) {
                        // Additional candidate with same length.
                        numMethodsFoundWithCurrentMinimumArgs++;
                    }
                }
            }
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName +
                    "' to a unique method. Attempted to resolve to overloaded method with " +
                    "the least number of parameters, but there were " +
                    numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }

    /**
     * Find a method with the given method name and minimal parameters (best case: none),
     * declared on the given class or one of its superclasses. Will return a public,
     * protected, package access, or private method.
     * <p>Checks <code>Class.getDeclaredMethods</code>, cascading upwards to all superclasses.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @return the Method object, or <code>null</code> if not found
     * @throws IllegalArgumentException if methods of the given name were found but
     * could not be resolved to a unique method with minimal parameters
     * @see java.lang.Class#getDeclaredMethods
     */
    public static Method findDeclaredMethodWithMinimalParameters(Class clazz, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = doFindMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null && clazz.getSuperclass() != null) {
            return findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
        }
        return targetMethod;
    }

    /**
     * Check if the given type represents a "simple" property:
     * a primitive, a String or other CharSequence, a Number, a Date,
     * a URI, a URL, a Locale, a Class, or a corresponding array.
     * <p>Used to determine properties to check for a "simple" dependency-check.
     * @param clazz the type to check
     * @return whether the given type represents a "simple" property
     * @see org.springframework.beans.factory.support.RootBeanDefinition#DEPENDENCY_CHECK_SIMPLE
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#checkDependencies
     */
    public static boolean isSimpleProperty(Class clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
    }

    /**
     * Check if the given type represents a "simple" value type:
     * a primitive, a String or other CharSequence, a Number, a Date,
     * a URI, a URL, a Locale or a Class.
     * @param clazz the type to check
     * @return whether the given type represents a "simple" value type
     */
    public static boolean isSimpleValueType(Class clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || CharSequence.class.isAssignableFrom(clazz) ||
                Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) ||
                clazz.equals(URI.class) || clazz.equals(URL.class) ||
                clazz.equals(Locale.class) || clazz.equals(Class.class);
    }

    /**
     * Find a JavaBeans <code>PropertyDescriptor</code> for the given method,
     * with the method either being the read method or the write method for
     * that bean property.
     * @param method the method to find a corresponding PropertyDescriptor for
     * @return the corresponding PropertyDescriptor, or <code>null</code> if none
     * @throws BeansException if PropertyDescriptor lookup fails
     */
    public static PropertyDescriptor findPropertyForMethod(Method method) throws BeansException {
        Assert.notNull(method, "Method must not be null");
        PropertyDescriptor[] pds = getPropertyDescriptors(method.getDeclaringClass());
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) {
                return pd;
            }
        }
        return null;
    }
}
