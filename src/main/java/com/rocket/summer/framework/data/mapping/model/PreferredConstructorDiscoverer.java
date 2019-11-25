package com.rocket.summer.framework.data.mapping.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

import com.rocket.summer.framework.core.DefaultParameterNameDiscoverer;
import com.rocket.summer.framework.core.ParameterNameDiscoverer;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PreferredConstructor;
import com.rocket.summer.framework.data.mapping.PreferredConstructor.Parameter;
import com.rocket.summer.framework.data.util.ClassTypeInformation;
import com.rocket.summer.framework.data.util.TypeInformation;

/**
 * Helper class to find a {@link PreferredConstructor}.
 *
 * @author Oliver Gierke
 * @author Roman Rodov
 */
public class PreferredConstructorDiscoverer<T, P extends PersistentProperty<P>> {

    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    private PreferredConstructor<T, P> constructor;

    /**
     * Creates a new {@link PreferredConstructorDiscoverer} for the given type.
     *
     * @param type must not be {@literal null}.
     */
    public PreferredConstructorDiscoverer(Class<T> type) {
        this(ClassTypeInformation.from(type), null);
    }

    /**
     * Creates a new {@link PreferredConstructorDiscoverer} for the given {@link PersistentEntity}.
     *
     * @param entity must not be {@literal null}.
     */
    public PreferredConstructorDiscoverer(PersistentEntity<T, P> entity) {
        this(entity.getTypeInformation(), entity);
    }

    /**
     * Creates a new {@link PreferredConstructorDiscoverer} for the given type.
     *
     * @param type must not be {@literal null}.
     * @param entity
     */
    protected PreferredConstructorDiscoverer(TypeInformation<T> type, PersistentEntity<T, P> entity) {

        boolean noArgConstructorFound = false;
        int numberOfArgConstructors = 0;
        Class<?> rawOwningType = type.getType();

        for (Constructor<?> candidate : rawOwningType.getDeclaredConstructors()) {

            PreferredConstructor<T, P> preferredConstructor = buildPreferredConstructor(candidate, type, entity);

            // Synthetic constructors should not be considered
            if (preferredConstructor.getConstructor().isSynthetic()) {
                continue;
            }

            // Explicitly defined constructor trumps all
            if (preferredConstructor.isExplicitlyAnnotated()) {
                this.constructor = preferredConstructor;
                return;
            }

            // No-arg constructor trumps custom ones
            if (this.constructor == null || preferredConstructor.isNoArgConstructor()) {
                this.constructor = preferredConstructor;
            }

            if (preferredConstructor.isNoArgConstructor()) {
                noArgConstructorFound = true;
            } else {
                numberOfArgConstructors++;
            }
        }

        if (!noArgConstructorFound && numberOfArgConstructors > 1) {
            this.constructor = null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private PreferredConstructor<T, P> buildPreferredConstructor(Constructor<?> constructor,
                                                                 TypeInformation<T> typeInformation, PersistentEntity<T, P> entity) {

        List<TypeInformation<?>> parameterTypes = typeInformation.getParameterTypes(constructor);

        if (parameterTypes.isEmpty()) {
            return new PreferredConstructor<T, P>((Constructor<T>) constructor);
        }

        String[] parameterNames = discoverer.getParameterNames(constructor);

        Parameter<Object, P>[] parameters = new Parameter[parameterTypes.size()];
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

        for (int i = 0; i < parameterTypes.size(); i++) {

            String name = parameterNames == null ? null : parameterNames[i];
            TypeInformation<?> type = parameterTypes.get(i);
            Annotation[] annotations = parameterAnnotations[i];

            parameters[i] = new Parameter(name, type, annotations, entity);
        }

        return new PreferredConstructor<T, P>((Constructor<T>) constructor, parameters);
    }

    /**
     * Returns the discovered {@link PreferredConstructor}.
     *
     * @return
     */
    public PreferredConstructor<T, P> getConstructor() {
        return constructor;
    }
}

