package com.rocket.summer.framework.util;

import java.beans.Introspector;
import java.lang.reflect.*;
import java.util.*;

public abstract class ClassUtils {

    /** Suffix for array class names: "[]" */
    public static final String ARRAY_SUFFIX = "[]";

    /** Prefix for internal array class names: "[L" */
    private static final String INTERNAL_ARRAY_PREFIX = "[L";

    /** The package separator character '.' */
    private static final char PACKAGE_SEPARATOR = '.';

    /** The inner class separator character '$' */
    private static final char INNER_CLASS_SEPARATOR = '$';

    /** The CGLIB class separator character "$$" */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    /** The ".class" file suffix */
    public static final String CLASS_FILE_SUFFIX = ".class";


    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);

    /**
     * Map with primitive type name as key and corresponding primitive
     * type as value, for example: "int" -> "int.class".
     */
    private static final Map primitiveTypeNameMap = new HashMap(16);

    /**
     * Map with primitive type as key and corresponding wrapper
     * type as value, for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);


    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        Set primitiveTypeNames = new HashSet(16);
        primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
        primitiveTypeNames.addAll(Arrays.asList(new Class[] {
                boolean[].class, byte[].class, char[].class, double[].class,
                float[].class, int[].class, long[].class, short[].class}));
        for (Iterator it = primitiveTypeNames.iterator(); it.hasNext();) {
            Class primitiveClass = (Class) it.next();
            primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
        }
    }

    /**
     * Determine the name of the package of the given fully-qualified class name,
     * e.g. "java.lang" for the {@code java.lang.String} class name.
     * @param fqClassName the fully-qualified class name
     * @return the package name, or the empty String if the class
     * is defined in the default package
     */
    public static String getPackageName(String fqClassName) {
        Assert.notNull(fqClassName, "Class name must not be null");
        int lastDotIndex = fqClassName.lastIndexOf(PACKAGE_SEPARATOR);
        return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
    }

    /**
     * Determine the name of the package of the given class,
     * e.g. "java.lang" for the {@code java.lang.String} class.
     * @param clazz the class
     * @return the package name, or the empty String if the class
     * is defined in the default package
     */
    public static String getPackageName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return getPackageName(clazz.getName());
    }

    /**
     * Determine whether the given class has a method with the given signature,
     * and return it if available (else return <code>null</code>).
     * <p>Essentially translates <code>NoSuchMethodException</code> to <code>null</code>.
     * @param clazz	the clazz to analyze
     * @param methodName the name of the method
     * @param paramTypes the parameter types of the method
     * @return the method, or <code>null</code> if not found
     * @see java.lang.Class#getMethod
     */
    public static Method getMethodIfAvailable(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Copy the given {@code Collection} into a {@code Class} array.
     * <p>The {@code Collection} must contain {@code Class} elements only.
     * @param collection the {@code Collection} to copy
     * @return the {@code Class} array
     * @since 3.1
     * @see StringUtils#toStringArray
     */
    public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new Class<?>[collection.size()]);
    }

    /**
     * Override the thread context ClassLoader with the environment's bean ClassLoader
     * if necessary, i.e. if the bean ClassLoader is not equivalent to the thread
     * context ClassLoader already.
     * @param classLoaderToUse the actual ClassLoader to use for the thread context
     * @return the original thread context ClassLoader, or {@code null} if not overridden
     */
    public static ClassLoader overrideThreadContextClassLoader(ClassLoader classLoaderToUse) {
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
            currentThread.setContextClassLoader(classLoaderToUse);
            return threadContextClassLoader;
        }
        else {
            return null;
        }
    }

    /**
     * Return a descriptive name for the given object's type: usually simply
     * the class name, but component type class name + "[]" for arrays,
     * and an appended list of implemented interfaces for JDK proxies.
     * @param value the value to introspect
     * @return the qualified name of the class
     */
    public static String getDescriptiveType(Object value) {
        if (value == null) {
            return null;
        }
        Class clazz = value.getClass();
        if (Proxy.isProxyClass(clazz)) {
            StringBuffer buf = new StringBuffer(clazz.getName());
            buf.append(" implementing ");
            Class[] ifcs = clazz.getInterfaces();
            for (int i = 0; i < ifcs.length; i++) {
                buf.append(ifcs[i].getName());
                if (i < ifcs.length - 1) {
                    buf.append(',');
                }
            }
            return buf.toString();
        }
        else if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        }
        else {
            return clazz.getName();
        }
    }

    /**
     * Return a public static method of a class.
     * @param methodName the static method name
     * @param clazz	the class which defines the method
     * @param args the parameter types to the method
     * @return the static method, or <code>null</code> if no static method was found
     * @throws IllegalArgumentException if the method name is blank or the clazz is null
     */
    public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        try {
            Method method = clazz.getMethod(methodName, args);
            return Modifier.isStatic(method.getModifiers()) ? method : null;
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Determine the name of the class file, relative to the containing
     * package: e.g. "String.class"
     * @param clazz the class
     * @return the file name of the ".class" file
     */
    public static String getClassFileName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
    }

    /**
     * Return all interfaces that the given instance implements as array,
     * including ones implemented by superclasses.
     * @param instance the instance to analyse for interfaces
     * @return all interfaces that the given instance implements as array
     */
    public static Class[] getAllInterfaces(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClass(instance.getClass());
    }

    /**
     * Build a String that consists of the names of the classes/interfaces
     * in the given collection.
     * <p>Basically like <code>AbstractCollection.toString()</code>, but stripping
     * the "class "/"interface " prefix before every class name.
     * @param classes a Collection of Class objects (may be <code>null</code>)
     * @return a String of form "[com.foo.Bar, com.foo.Baz]"
     * @see java.util.AbstractCollection#toString()
     */
    public static String classNamesToString(Collection classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return "[]";
        }
        StringBuffer sb = new StringBuffer("[");
        for (Iterator it = classes.iterator(); it.hasNext(); ) {
            Class clazz = (Class) it.next();
            sb.append(clazz.getName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Resolve the given class if it is a primitive class,
     * returning the corresponding primitive wrapper type instead.
     * @param clazz the class to check
     * @return the original class, or a primitive wrapper for the original primitive type
     */
    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() && clazz != void.class? primitiveTypeToWrapperMap.get(clazz) : clazz);
    }

    /**
     * Build a nice qualified name for an array:
     * component type class name + "[]".
     * @param clazz the array class
     * @return a qualified name for the array class
     */
    private static String getQualifiedNameForArray(Class clazz) {
        StringBuffer buffer = new StringBuffer();
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            buffer.append(ClassUtils.ARRAY_SUFFIX);
        }
        buffer.insert(0, clazz.getName());
        return buffer.toString();
    }

    /**
     * Return the qualified name of the given class: usually simply
     * the class name, but component type class name + "[]" for arrays.
     * @param clazz the class
     * @return the qualified name of the class
     */
    public static String getQualifiedName(Class clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        }
        else {
            return clazz.getName();
        }
    }

    /**
     * Determine whether the given class has a public constructor with the given signature.
     * <p>Essentially translates <code>NoSuchMethodException</code> to "false".
     * @param clazz	the clazz to analyze
     * @param paramTypes the parameter types of the method
     * @return whether the class has a corresponding constructor
     * @see java.lang.Class#getMethod
     */
    public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
        return (getConstructorIfAvailable(clazz, paramTypes) != null);
    }

    /**
     * Determine whether the given class has a public constructor with the given signature,
     * and return it if available (else return <code>null</code>).
     * <p>Essentially translates <code>NoSuchMethodException</code> to <code>null</code>.
     * @param clazz	the clazz to analyze
     * @param paramTypes the parameter types of the method
     * @return the constructor, or <code>null</code> if not found
     * @see java.lang.Class#getConstructor
     */
    public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        try {
            return clazz.getConstructor(paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you absolutely need a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
     * reference as well).
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    /**
     * Resolve the given class name into a Class instance. Supports
     * primitives (like "int") and array class names (like "String[]").
     * <p>This is effectively equivalent to the <code>forName</code>
     * method with the same arguments, with the only difference being
     * the exceptions thrown in case of class loading failure.
     * @param className the name of the Class
     * @param classLoader the class loader to use
     * (may be <code>null</code>, which indicates the default class loader)
     * @return Class instance for the supplied name
     * @throws IllegalArgumentException if the class name was not resolvable
     * (that is, the class could not be found or the class file could not be loaded)
     * @see #forName(String, ClassLoader)
     */
    public static Class resolveClassName(String className, ClassLoader classLoader) throws IllegalArgumentException {
        try {
            return forName(className, classLoader);
        }
        catch (ClassNotFoundException ex) {
            IllegalArgumentException iae = new IllegalArgumentException("Cannot find class [" + className + "]");
            iae.initCause(ex);
            throw iae;
        }
        catch (LinkageError ex) {
            IllegalArgumentException iae = new IllegalArgumentException(
                    "Error loading class [" + className + "]: problem with class file or dependent class.");
            iae.initCause(ex);
            throw iae;
        }
    }

    /**
     * Replacement for <code>Class.forName()</code> that also returns Class instances
     * for primitives (like "int") and array class names (like "String[]").
     * <p>Always uses the default class loader: that is, preferably the thread context
     * class loader, or the ClassLoader that loaded the ClassUtils class as fallback.
     * @param name the name of the Class
     * @return Class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     * @see #getDefaultClassLoader()
     */
    public static Class forName(String name) throws ClassNotFoundException, LinkageError {
        return forName(name, getDefaultClassLoader());
    }

    /**
     * Replacement for <code>Class.forName()</code> that also returns Class instances
     * for primitives (like "int") and array class names (like "String[]").
     * @param name the name of the Class
     * @param classLoader the class loader to use
     * (may be <code>null</code>, which indicates the default class loader)
     * @return Class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");

        Class clazz = resolvePrimitiveClassName(name);
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
        if (internalArrayMarker != -1 && name.endsWith(";")) {
            String elementClassName = null;
            if (internalArrayMarker == 0) {
                elementClassName = name.substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
            }
            else if (name.startsWith("[")) {
                elementClassName = name.substring(1);
            }
            Class elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getDefaultClassLoader();
        }
        return classLoaderToUse.loadClass(name);
    }

    /**
     * Resolve the given class name as primitive class, if appropriate,
     * according to the JVM's naming rules for primitive classes.
     * <p>Also supports the JVM's internal class names for primitive arrays.
     * Does <i>not</i> support the "[]" suffix notation for primitive arrays;
     * this is only supported by {@link #forName}.
     * @param name the name of the potentially primitive class
     * @return the primitive class, or <code>null</code> if the name does not denote
     * a primitive class or primitive array class
     */
    public static Class resolvePrimitiveClassName(String name) {
        Class result = null;
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            result = (Class) primitiveTypeNameMap.get(name);
        }
        return result;
    }

    /**
     * Determine if the given type is assignable from the given value,
     * assuming setting by reflection. Considers primitive wrapper classes
     * as assignable to the corresponding primitive types.
     * @param type	the target type
     * @param value the value that should be assigned to the type
     * @return if the type is assignable from the value
     */
    public static boolean isAssignableValue(Class type, Object value) {
        Assert.notNull(type, "Type must not be null");
        return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
    }

    /**
     * Check if the right-hand side type may be assigned to the left-hand side
     * type, assuming setting by reflection. Considers primitive wrapper
     * classes as assignable to the corresponding primitive types.
     * @param lhsType the target type
     * @param rhsType	the value type that should be assigned to the target type
     * @return if the target type is assignable from the value type
     * @see TypeUtils#isAssignable
     */
    public static boolean isAssignable(Class lhsType, Class rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        return (lhsType.isAssignableFrom(rhsType) ||
                lhsType.equals(primitiveWrapperTypeMap.get(rhsType)));
    }

   /**
     * Determine whether the given class has a method with the given signature.
     * <p>Essentially translates <code>NoSuchMethodException</code> to "false".
     * @param clazz	the clazz to analyze
     * @param methodName the name of the method
     * @param paramTypes the parameter types of the method
     * @return whether the class has a corresponding method
     * @see java.lang.Class#getMethod
     */
    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
    }

    /**
     * Return the number of methods with a given name (with any argument types),
     * for the given class and/or its superclasses. Includes non-public methods.
     * @param clazz	the clazz to check
     * @param methodName the name of the method
     * @return the number of methods with the given name
     */
    public static int getMethodCountForName(Class clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        int count = 0;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            if (methodName.equals(method.getName())) {
                count++;
            }
        }
        Class[] ifcs = clazz.getInterfaces();
        for (int i = 0; i < ifcs.length; i++) {
            count += getMethodCountForName(ifcs[i], methodName);
        }
        if (clazz.getSuperclass() != null) {
            count += getMethodCountForName(clazz.getSuperclass(), methodName);
        }
        return count;
    }

    /**
     * Check if the given class represents a primitive (i.e. boolean, byte,
     * char, short, int, long, float, or double) or a primitive wrapper
     * (i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
     * @param clazz the class to check
     * @return whether the given class is a primitive or primitive wrapper class
     */
    public static boolean isPrimitiveOrWrapper(Class clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * Check if the given class represents a primitive wrapper,
     * i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
     * @param clazz the class to check
     * @return whether the given class is a primitive wrapper class
     */
    public static boolean isPrimitiveWrapper(Class clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    /**
     * Return all interfaces that the given class implements as array,
     * including ones implemented by superclasses.
     * <p>If the class itself is an interface, it gets returned as sole interface.
     * @param clazz the class to analyse for interfaces
     * @return all interfaces that the given object implements as array
     */
    public static Class[] getAllInterfacesForClass(Class clazz) {
        return getAllInterfacesForClass(clazz, null);
    }

    /**
     * Return all interfaces that the given class implements as array,
     * including ones implemented by superclasses.
     * <p>If the class itself is an interface, it gets returned as sole interface.
     * @param clazz the class to analyse for interfaces
     * @param classLoader the ClassLoader that the interfaces need to be visible in
     * (may be <code>null</code> when accepting all declared interfaces)
     * @return all interfaces that the given object implements as array
     */
    public static Class[] getAllInterfacesForClass(Class clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            return new Class[] {clazz};
        }
        List interfaces = new ArrayList();
        while (clazz != null) {
            for (int i = 0; i < clazz.getInterfaces().length; i++) {
                Class ifc = clazz.getInterfaces()[i];
                if (!interfaces.contains(ifc) &&
                        (classLoader == null || isVisible(ifc, classLoader))) {
                    interfaces.add(ifc);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    /**
     * Return all interfaces that the given instance implements as Set,
     * including ones implemented by superclasses.
     * @param instance the instance to analyse for interfaces
     * @return all interfaces that the given instance implements as Set
     */
    public static Set getAllInterfacesAsSet(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClassAsSet(instance.getClass());
    }

    /**
     * Return all interfaces that the given class implements as Set,
     * including ones implemented by superclasses.
     * <p>If the class itself is an interface, it gets returned as sole interface.
     * @param clazz the class to analyse for interfaces
     * @return all interfaces that the given object implements as Set
     */
    public static Set getAllInterfacesForClassAsSet(Class clazz) {
        return getAllInterfacesForClassAsSet(clazz, null);
    }

    /**
     * Return all interfaces that the given class implements as Set,
     * including ones implemented by superclasses.
     * <p>If the class itself is an interface, it gets returned as sole interface.
     * @param clazz the class to analyse for interfaces
     * @param classLoader the ClassLoader that the interfaces need to be visible in
     * (may be <code>null</code> when accepting all declared interfaces)
     * @return all interfaces that the given object implements as Set
     */
    public static Set getAllInterfacesForClassAsSet(Class clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            return Collections.singleton(clazz);
        }
        Set interfaces = new LinkedHashSet();
        while (clazz != null) {
            for (int i = 0; i < clazz.getInterfaces().length; i++) {
                Class ifc = clazz.getInterfaces()[i];
                if (classLoader == null || isVisible(ifc, classLoader)) {
                    interfaces.add(ifc);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return interfaces;
    }

    /**
     * Check whether the given class is visible in the given ClassLoader.
     * @param clazz the class to check (typically an interface)
     * @param classLoader the ClassLoader to check against (may be <code>null</code>,
     * in which case this method will always return <code>true</code>)
     */
    public static boolean isVisible(Class clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        try {
            Class actualClass = classLoader.loadClass(clazz.getName());
            return (clazz == actualClass);
            // Else: different interface class found...
        }
        catch (ClassNotFoundException ex) {
            // No interface class found...
            return false;
        }
    }

    /**
     * Check whether the given class is cache-safe in the given context,
     * i.e. whether it is loaded by the given ClassLoader or a parent of it.
     * @param clazz the class to analyze
     * @param classLoader the ClassLoader to potentially cache metadata in
     */
    public static boolean isCacheSafe(Class clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        ClassLoader target = clazz.getClassLoader();
        if (target == null) {
            return false;
        }
        ClassLoader cur = classLoader;
        if (cur == target) {
            return true;
        }
        while (cur != null) {
            cur = cur.getParent();
            if (cur == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the class name without the qualified package name.
     * @param clazz the class to get the short name for
     * @return the class name of the class without the package name
     */
    public static String getShortName(Class clazz) {
        return getShortName(getQualifiedName(clazz));
    }

    /**
     * Get the class name without the qualified package name.
     * @param className the className to get the short name for
     * @return the class name of the class without the package name
     * @throws IllegalArgumentException if the className is empty
     */
    public static String getShortName(String className) {
        Assert.hasLength(className, "Class name must not be empty");
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
        return shortName;
    }

    /**
     * Return the user-defined class for the given instance: usually simply
     * the class of the given instance, but the original class in case of a
     * CGLIB-generated subclass.
     * @param instance the instance to check
     * @return the user-defined class
     */
    public static Class getUserClass(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getUserClass(instance.getClass());
    }

    /**
     * Convert a "."-based fully qualified class name to a "/"-based resource path.
     * @param className the fully qualified class name
     * @return the corresponding resource path, pointing to the class
     */
    public static String convertClassNameToResourcePath(String className) {
        return className.replace('.', '/');
    }

    /**
     * Convert a "/"-based resource path to a "."-based fully qualified class name.
     * @param resourcePath the resource path pointing to a class
     * @return the corresponding fully qualified class name
     */
    public static String convertResourcePathToClassName(String resourcePath) {
        return resourcePath.replace('/', '.');
    }

    /**
     * Determine whether the {@link Class} identified by the supplied name is present
     * and can be loaded. Will return <code>false</code> if either the class or
     * one of its dependencies is not present or cannot be loaded.
     * @param className the name of the class to check
     * @param classLoader the class loader to use
     * (may be <code>null</code>, which indicates the default class loader)
     * @return whether the specified class is present
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        }
        catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }

    /**
     * Given a method, which may come from an interface, and a target class used
     * in the current reflective invocation, find the corresponding target method
     * if there is one. E.g. the method may be <code>IFoo.bar()</code> and the
     * target class may be <code>DefaultFoo</code>. In this case, the method may be
     * <code>DefaultFoo.bar()</code>. This enables attributes on that method to be found.
     * <p><b>NOTE:</b> In contrast to {@link com.rocket.summer.framework.aop.support.AopUtils#getMostSpecificMethod},
     * this method does <i>not</i> resolve Java 5 bridge methods automatically.
     * Call {@link com.rocket.summer.framework.core.BridgeMethodResolver#findBridgedMethod}
     * if bridge method resolution is desirable (e.g. for obtaining metadata from
     * the original method definition).
     * @param method the method to be invoked, which may come from an interface
     * @param targetClass the target class for the current invocation.
     * May be <code>null</code> or may not even implement the method.
     * @return the specific target method, or the original method if the
     * <code>targetClass</code> doesn't implement it or is <code>null</code>
     * @see com.rocket.summer.framework.aop.support.AopUtils#getMostSpecificMethod
     */
    public static Method getMostSpecificMethod(Method method, Class targetClass) {
        if (method != null && targetClass != null && !targetClass.equals(method.getDeclaringClass())) {
            try {
                method = targetClass.getMethod(method.getName(), method.getParameterTypes());
            }
            catch (NoSuchMethodException ex) {
                // Perhaps the target class doesn't implement this method:
                // that's fine, just use the original method.
            }
        }
        return method;
    }

    /**
     * Return the short string name of a Java class in decapitalized JavaBeans
     * property format. Strips the outer class name in case of an inner class.
     * @param clazz the class
     * @return the short name rendered in a standard JavaBeans property format
     * @see java.beans.Introspector#decapitalize(String)
     */
    public static String getShortNameAsProperty(Class clazz) {
        String shortName = ClassUtils.getShortName(clazz);
        int dotIndex = shortName.lastIndexOf('.');
        shortName = (dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName);
        return Introspector.decapitalize(shortName);
    }
}
