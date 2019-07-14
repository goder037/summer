package com.rocket.summer.framework.core;

import com.rocket.summer.framework.util.Assert;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class GenericTypeResolver {

    /** Cache from Class to TypeVariable Map */
    private static final Map typeVariableCache = Collections.synchronizedMap(new WeakHashMap());


    /**
     * Determine the target type for the given parameter specification.
     * @param methodParam the method parameter specification
     * @return the corresponding generic parameter type
     */
    public static Type getTargetType(MethodParameter methodParam) {
        Assert.notNull(methodParam, "MethodParameter must not be null");
        if (methodParam.getConstructor() != null) {
            return methodParam.getConstructor().getGenericParameterTypes()[methodParam.getParameterIndex()];
        }
        else {
            if (methodParam.getParameterIndex() >= 0) {
                return methodParam.getMethod().getGenericParameterTypes()[methodParam.getParameterIndex()];
            }
            else {
                return methodParam.getMethod().getGenericReturnType();
            }
        }
    }

    /**
     * Build a mapping of {@link TypeVariable#getName TypeVariable names} to concrete
     * {@link Class} for the specified {@link Class}. Searches all super types,
     * enclosing types and interfaces.
     */
    static Map getTypeVariableMap(Class clazz) {
        Reference ref = (Reference) typeVariableCache.get(clazz);
        Map typeVariableMap = (Map) (ref != null ? ref.get() : null);

        if (typeVariableMap == null) {
            typeVariableMap = new HashMap();

            // interfaces
            extractTypeVariablesFromGenericInterfaces(clazz.getGenericInterfaces(), typeVariableMap);

            // super class
            Type genericType = clazz.getGenericSuperclass();
            Class type = clazz.getSuperclass();
            while (type != null && !Object.class.equals(type)) {
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    populateTypeMapFromParameterizedType(pt, typeVariableMap);
                }
                extractTypeVariablesFromGenericInterfaces(type.getGenericInterfaces(), typeVariableMap);
                genericType = type.getGenericSuperclass();
                type = type.getSuperclass();
            }

            // enclosing class
            type = clazz;
            while (type.isMemberClass()) {
                genericType = type.getGenericSuperclass();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    populateTypeMapFromParameterizedType(pt, typeVariableMap);
                }
                type = type.getEnclosingClass();
            }

            typeVariableCache.put(clazz, new WeakReference(typeVariableMap));
        }

        return typeVariableMap;
    }
    /**
     * Read the {@link TypeVariable TypeVariables} from the supplied {@link ParameterizedType}
     * and add mappings corresponding to the {@link TypeVariable#getName TypeVariable name} ->
     * concrete type to the supplied {@link Map}.
     * <p>Consider this case:
     * <pre class="code>
     * public interface Foo<S, T> {
     *  ..
     * }
     *
     * public class FooImpl implements Foo<String, Integer> {
     *  ..
     * }</pre>
     * For '<code>FooImpl</code>' the following mappings would be added to the {@link Map}:
     * {S=java.lang.String, T=java.lang.Integer}.
     */
    private static void populateTypeMapFromParameterizedType(ParameterizedType type, Map typeVariableMap) {
        if (type.getRawType() instanceof Class) {
            Type[] actualTypeArguments = type.getActualTypeArguments();
            TypeVariable[] typeVariables = ((Class) type.getRawType()).getTypeParameters();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArgument = actualTypeArguments[i];
                TypeVariable variable = typeVariables[i];
                if (actualTypeArgument instanceof Class) {
                    typeVariableMap.put(variable, actualTypeArgument);
                }
                else if (actualTypeArgument instanceof GenericArrayType) {
                    typeVariableMap.put(variable, actualTypeArgument);
                }
                else if (actualTypeArgument instanceof ParameterizedType) {
                    typeVariableMap.put(variable, actualTypeArgument);
                }
                else if (actualTypeArgument instanceof TypeVariable) {
                    // We have a type that is parameterized at instantiation time
                    // the nearest match on the bridge method will be the bounded type.
                    TypeVariable typeVariableArgument = (TypeVariable) actualTypeArgument;
                    Type resolvedType = (Type) typeVariableMap.get(typeVariableArgument);
                    if (resolvedType == null) {
                        resolvedType = extractBoundForTypeVariable(typeVariableArgument);
                    }
                    typeVariableMap.put(variable, resolvedType);
                }
            }
        }
    }

    /**
     * Extracts the bound <code>Type</code> for a given {@link TypeVariable}.
     */
    static Type extractBoundForTypeVariable(TypeVariable typeVariable) {
        Type[] bounds = typeVariable.getBounds();
        if (bounds.length == 0) {
            return Object.class;
        }
        Type bound = bounds[0];
        if (bound instanceof TypeVariable) {
            bound = extractBoundForTypeVariable((TypeVariable) bound);
        }
        return bound;
    }

    private static void extractTypeVariablesFromGenericInterfaces(Type[] genericInterfaces, Map typeVariableMap) {
        for (int i = 0; i < genericInterfaces.length; i++) {
            Type genericInterface = genericInterfaces[i];
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                populateTypeMapFromParameterizedType(pt, typeVariableMap);
                if (pt.getRawType() instanceof Class) {
                    extractTypeVariablesFromGenericInterfaces(
                            ((Class) pt.getRawType()).getGenericInterfaces(), typeVariableMap);
                }
            }
            else if (genericInterface instanceof Class) {
                extractTypeVariablesFromGenericInterfaces(
                        ((Class) genericInterface).getGenericInterfaces(), typeVariableMap);
            }
        }
    }

    /**
     * Determine the raw type for the given generic parameter type.
     * @param genericType the generic type to resolve
     * @param typeVariableMap the TypeVariable Map to resolved against
     * @return the resolved raw type
     */
    static Type getRawType(Type genericType, Map typeVariableMap) {
        Type resolvedType = genericType;
        if (genericType instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable) genericType;
            resolvedType = (Type) typeVariableMap.get(tv);
            if (resolvedType == null) {
                resolvedType = extractBoundForTypeVariable(tv);
            }
        }
        if (resolvedType instanceof ParameterizedType) {
            return ((ParameterizedType) resolvedType).getRawType();
        }
        else {
            return resolvedType;
        }
    }

    /**
     * Resolve the specified generic type against the given TypeVariable map.
     * @param genericType the generic type to resolve
     * @param typeVariableMap the TypeVariable Map to resolved against
     * @return the type if it resolves to a Class, or <code>Object.class</code> otherwise
     */
    static Class resolveType(Type genericType, Map typeVariableMap) {
        Type rawType = getRawType(genericType, typeVariableMap);
        return (rawType instanceof Class ? (Class) rawType : Object.class);
    }

    /**
     * Determine the target type for the generic return type of the given method.
     * @param method the method to introspect
     * @param clazz the class to resolve type variables against
     * @return the corresponding generic parameter or return type
     */
    public static Class resolveReturnType(Method method, Class clazz) {
        Assert.notNull(method, "Method must not be null");
        Type genericType = method.getGenericReturnType();
        Assert.notNull(clazz, "Class must not be null");
        Map typeVariableMap = getTypeVariableMap(clazz);
        Type rawType = getRawType(genericType, typeVariableMap);
        return (rawType instanceof Class ? (Class) rawType : method.getReturnType());
    }

    /**
     * Determine the target type for the given generic parameter type.
     * @param methodParam the method parameter specification
     * @param clazz the class to resolve type variables against
     * @return the corresponding generic parameter or return type
     */
    public static Class resolveParameterType(MethodParameter methodParam, Class clazz) {
        Type genericType = getTargetType(methodParam);
        Assert.notNull(clazz, "Class must not be null");
        Map typeVariableMap = getTypeVariableMap(clazz);
        Type rawType = getRawType(genericType, typeVariableMap);
        Class result = (rawType instanceof Class ? (Class) rawType : methodParam.getParameterType());
        methodParam.setParameterType(result);
        methodParam.typeVariableMap = typeVariableMap;
        return result;
    }
}
