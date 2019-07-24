package com.rocket.summer.framework.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ReflectionUtils {

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied <code>name</code>. Searches all superclasses up to {@link Object}.
     * @param clazz the class to introspect
     * @param name the name of the field
     * @return the corresponding Field object, or <code>null</code> if not found
     */
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied <code>name</code> and/or {@link Class type}. Searches all superclasses
     * up to {@link Object}.
     * @param clazz the class to introspect
     * @param name the name of the field (may be <code>null</code> if type is specified)
     * @param type the type of the field (may be <code>null</code> if name is specified)
     * @return the corresponding Field object, or <code>null</code> if not found
     */
    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with no arguments. The target object can be <code>null</code> when
     * invoking a static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(Method, Object, Object[])
     */
    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, null);
    }

      /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be <code>null</code> when invoking a
     * static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments (may be <code>null</code>)
     * @return the invocation result, if any
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        }
        catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * Determine whether the given method is an "equals" method.
     * @see java.lang.Object#equals
     */
    public static boolean isEqualsMethod(Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        Class[] paramTypes = method.getParameterTypes();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
    }

    /**
     * Determine whether the given method is a "hashCode" method.
     * @see java.lang.Object#hashCode
     */
    public static boolean isHashCodeMethod(Method method) {
        return (method != null && method.getName().equals("hashCode") &&
                method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is a "toString" method.
     * @see java.lang.Object#toString()
     */
    public static boolean isToStringMethod(Method method) {
        return (method != null && method.getName().equals("toString") &&
                method.getParameterTypes().length == 0);
    }

    /**
     * Handle the given reflection exception. Should only be called if
     * no checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of an
     * InvocationTargetException with such a root cause. Throws an
     * IllegalStateException with an appropriate message else.
     * @param ex the reflection exception to handle
     */
    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        handleUnexpectedException(ex);
    }

    /**
     * Handle the given invocation target exception. Should only be called if
     * no checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of such
     * a root cause. Throws an IllegalStateException else.
     * @param ex the invocation target exception to handle
     */
    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}.
     * Should only be called if no checked exception is expected to be thrown by
     * the target method.
     * <p>Rethrows the underlying exception cast to an {@link RuntimeException}
     * or {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * @param ex the exception to rethrow
     * @throws RuntimeException the rethrown exception
     */
    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        handleUnexpectedException(ex);
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}.
     * Should only be called if no checked exception is expected to be thrown by
     * the target method.
     * <p>Rethrows the underlying exception cast to an {@link Exception} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * @param ex the exception to rethrow
     * @throws Exception the rethrown exception (in case of a checked exception)
     */
    public static void rethrowException(Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        handleUnexpectedException(ex);
    }

    /**
     * Throws an IllegalStateException with the given exception as root cause.
     * @param ex the unexpected exception
     */
    private static void handleUnexpectedException(Throwable ex) {
        // Needs to avoid the chained constructor for JDK 1.4 compatibility.
        IllegalStateException isex = new IllegalStateException("Unexpected exception thrown");
        isex.initCause(ex);
        throw isex;
    }

    /**
     * Make the given constructor accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary,
     * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
     * @param ctor the constructor to make accessible
     * @see java.lang.reflect.Constructor#setAccessible
     */
    public static void makeAccessible(Constructor ctor) {
        if (!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) {
            ctor.setAccessible(true);
        }
    }

    /**
     * Make the given method accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary,
     * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
     * @param method the method to make accessible
     * @see java.lang.reflect.Method#setAccessible
     */
    public static void makeAccessible(Method method) {
        if (!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }
    }

    /**
     * Determine whether the given field is a "public static final" constant.
     * @param field the field to check
     */
    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and no parameters. Searches all superclasses up to <code>Object</code>.
     * <p>Returns <code>null</code> if no {@link Method} can be found.
     * @param clazz the class to introspect
     * @param name the name of the method
     * @return the Method object, or <code>null</code> if none found
     */
    public static Method findMethod(Class clazz, String name) {
        return findMethod(clazz, name, new Class[0]);
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to <code>Object</code>.
     * <p>Returns <code>null</code> if no {@link Method} can be found.
     * @param clazz the class to introspect
     * @param name the name of the method
     * @param paramTypes the parameter types of the method
     * (may be <code>null</code> to indicate any signature)
     * @return the Method object, or <code>null</code> if none found
     */
    public static Method findMethod(Class clazz, String name, Class[] paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        Class searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (name.equals(method.getName()) &&
                        (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary,
     * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    public static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * Invoke the given callback on all fields in the target class,
     * going up the class hierarchy to get all declared fields.
     * @param targetClass the target class to analyze
     * @param fc the callback to invoke for each field
     */
    public static void doWithFields(Class targetClass, FieldCallback fc) throws IllegalArgumentException {
        doWithFields(targetClass, fc, null);
    }

    /**
     * Invoke the given callback on all fields in the target class,
     * going up the class hierarchy to get all declared fields.
     * @param targetClass the target class to analyze
     * @param fc the callback to invoke for each field
     * @param ff the filter that determines the fields to apply the callback to
     */
    public static void doWithFields(Class targetClass, FieldCallback fc, FieldFilter ff)
            throws IllegalArgumentException {

        // Keep backing up the inheritance hierarchy.
        do {
            // Copy each field declared on this class unless it's static or file.
            Field[] fields = targetClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                // Skip static and final fields.
                if (ff != null && !ff.matches(fields[i])) {
                    continue;
                }
                try {
                    fc.doWith(fields[i]);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException(
                            "Shouldn't be illegal to access field '" + fields[i].getName() + "': " + ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    /**
     * Get all declared methods on the leaf class and all superclasses.
     * Leaf class methods are included first.
     */
    public static Method[] getAllDeclaredMethods(Class leafClass) throws IllegalArgumentException {
        final List list = new ArrayList(32);
        doWithMethods(leafClass, new MethodCallback() {
            public void doWith(Method method) {
                list.add(method);
            }
        });
        return (Method[]) list.toArray(new Method[list.size()]);
    }

    /**
     * Perform the given callback operation on all matching methods of the
     * given class and superclasses.
     * <p>The same named method occurring on subclass and superclass will
     * appear twice, unless excluded by a {@link MethodFilter}.
     * @param targetClass class to start looking at
     * @param mc the callback to invoke for each method
     * @see #doWithMethods(Class, MethodCallback, MethodFilter)
     */
    public static void doWithMethods(Class targetClass, MethodCallback mc) throws IllegalArgumentException {
        doWithMethods(targetClass, mc, null);
    }

    /**
     * Perform the given callback operation on all matching methods of the
     * given class and superclasses.
     * <p>The same named method occurring on subclass and superclass will
     * appear twice, unless excluded by the specified {@link MethodFilter}.
     * @param targetClass class to start looking at
     * @param mc the callback to invoke for each method
     * @param mf the filter that determines the methods to apply the callback to
     */
    public static void doWithMethods(Class targetClass, MethodCallback mc, MethodFilter mf)
            throws IllegalArgumentException {

        // Keep backing up the inheritance hierarchy.
        do {
            Method[] methods = targetClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (mf != null && !mf.matches(methods[i])) {
                    continue;
                }
                try {
                    mc.doWith(methods[i]);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException(
                            "Shouldn't be illegal to access method '" + methods[i].getName() + "': " + ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null);
    }

    /**
     * Action to take on each method.
     */
    public static interface MethodCallback {

        /**
         * Perform an operation using the given method.
         * @param method the method to operate on
         */
        void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
    }


    /**
     * Callback optionally used to method fields to be operated on by a method callback.
     */
    public static interface MethodFilter {

        /**
         * Determine whether the given method matches.
         * @param method the method to check
         */
        boolean matches(Method method);
    }


    /**
     * Callback interface invoked on each field in the hierarchy.
     */
    public static interface FieldCallback {

        /**
         * Perform an operation using the given field.
         * @param field the field to operate on
         */
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }


    /**
     * Callback optionally used to filter fields to be operated on by a field callback.
     */
    public static interface FieldFilter {

        /**
         * Determine whether the given field matches.
         * @param field the field to check
         */
        boolean matches(Field field);
    }

}
