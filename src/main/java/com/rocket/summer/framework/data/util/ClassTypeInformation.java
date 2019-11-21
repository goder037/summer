package com.rocket.summer.framework.data.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import com.rocket.summer.framework.core.GenericTypeResolver;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * {@link TypeInformation} for a plain {@link Class}.
 *
 * @author Oliver Gierke
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ClassTypeInformation<S> extends TypeDiscoverer<S> {

    public static final ClassTypeInformation<Collection> COLLECTION = new ClassTypeInformation(Collection.class);
    public static final ClassTypeInformation<List> LIST = new ClassTypeInformation(List.class);
    public static final ClassTypeInformation<Set> SET = new ClassTypeInformation(Set.class);
    public static final ClassTypeInformation<Map> MAP = new ClassTypeInformation(Map.class);
    public static final ClassTypeInformation<Object> OBJECT = new ClassTypeInformation(Object.class);

    private static final Map<Class<?>, Reference<ClassTypeInformation<?>>> CACHE = Collections
            .synchronizedMap(new WeakHashMap<Class<?>, Reference<ClassTypeInformation<?>>>());

    static {
        for (ClassTypeInformation<?> info : Arrays.asList(COLLECTION, LIST, SET, MAP, OBJECT)) {
            CACHE.put(info.getType(), new WeakReference<ClassTypeInformation<?>>(info));
        }
    }

    private final Class<S> type;

    /**
     * Simple factory method to easily create new instances of {@link ClassTypeInformation}.
     *
     * @param <S>
     * @param type must not be {@literal null}.
     * @return
     */
    public static <S> ClassTypeInformation<S> from(Class<S> type) {

        Assert.notNull(type, "Type must not be null!");

        Reference<ClassTypeInformation<?>> cachedReference = CACHE.get(type);
        TypeInformation<?> cachedTypeInfo = cachedReference == null ? null : cachedReference.get();

        if (cachedTypeInfo != null) {
            return (ClassTypeInformation<S>) cachedTypeInfo;
        }

        ClassTypeInformation<S> result = new ClassTypeInformation<S>(type);
        CACHE.put(type, new WeakReference<ClassTypeInformation<?>>(result));
        return result;
    }

    /**
     * Creates a {@link TypeInformation} from the given method's return type.
     *
     * @param method must not be {@literal null}.
     * @return
     */
    public static <S> TypeInformation<S> fromReturnTypeOf(Method method) {

        Assert.notNull(method, "Method must not be null!");
        return (TypeInformation<S>) ClassTypeInformation.from(method.getDeclaringClass())
                .createInfo(method.getGenericReturnType());
    }

    /**
     * Creates {@link ClassTypeInformation} for the given type.
     *
     * @param type
     */
    ClassTypeInformation(Class<S> type) {
        super(ProxyUtils.getUserClass(type), getTypeVariableMap(type));
        this.type = type;
    }

    /**
     * Little helper to allow us to create a generified map, actually just to satisfy the compiler.
     *
     * @param type must not be {@literal null}.
     * @return
     */
    private static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> type) {
        return getTypeVariableMap(type, new HashSet<Type>());
    }

    @SuppressWarnings("deprecation")
    private static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> type, Collection<Type> visited) {

        if (visited.contains(type)) {
            return Collections.emptyMap();
        } else {
            visited.add(type);
        }

        Map<TypeVariable, Type> source = GenericTypeResolver.getTypeVariableMap(type);
        Map<TypeVariable<?>, Type> map = new HashMap<TypeVariable<?>, Type>(source.size());

        for (Entry<TypeVariable, Type> entry : source.entrySet()) {

            Type value = entry.getValue();
            map.put(entry.getKey(), entry.getValue());

            if (value instanceof Class) {

                for (Entry<TypeVariable<?>, Type> nestedEntry : getTypeVariableMap((Class<?>) value, visited).entrySet()) {
                    if (!map.containsKey(nestedEntry.getKey())) {
                        map.put(nestedEntry.getKey(), nestedEntry.getValue());
                    }
                }
            }
        }

        return map;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.TypeDiscoverer#getType()
     */
    @Override
    public Class<S> getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.TypeDiscoverer#getRawTypeInformation()
     */
    @Override
    public ClassTypeInformation<?> getRawTypeInformation() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.TypeDiscoverer#isAssignableFrom(com.rocket.summer.framework.data.util.TypeInformation)
     */
    @Override
    public boolean isAssignableFrom(TypeInformation<?> target) {
        return getType().isAssignableFrom(target.getType());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.util.TypeDiscoverer#specialize(com.rocket.summer.framework.data.util.ClassTypeInformation)
     */
    @Override
    public TypeInformation<?> specialize(ClassTypeInformation<?> type) {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return type.getName();
    }
}

