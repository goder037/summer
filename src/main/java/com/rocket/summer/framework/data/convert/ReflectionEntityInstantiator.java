package com.rocket.summer.framework.data.convert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.beans.BeanInstantiationException;
import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PreferredConstructor;
import com.rocket.summer.framework.data.mapping.PreferredConstructor.Parameter;
import com.rocket.summer.framework.data.mapping.model.MappingInstantiationException;
import com.rocket.summer.framework.data.mapping.model.ParameterValueProvider;

/**
 * {@link EntityInstantiator} that uses the {@link PersistentEntity}'s {@link PreferredConstructor} to instantiate an
 * instance of the entity via reflection.
 *
 * @author Oliver Gierke
 */
public enum ReflectionEntityInstantiator implements EntityInstantiator {

    INSTANCE;

    @SuppressWarnings("unchecked")
    public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity,
                                                                                                             ParameterValueProvider<P> provider) {

        PreferredConstructor<? extends T, P> constructor = entity.getPersistenceConstructor();

        if (constructor == null) {

            try {
                Class<?> clazz = entity.getType();
                if (clazz.isArray()) {
                    Class<?> ctype = clazz;
                    int dims = 0;
                    while (ctype.isArray()) {
                        ctype = ctype.getComponentType();
                        dims++;
                    }
                    return (T) Array.newInstance(clazz, dims);
                } else {
                    return BeanUtils.instantiateClass(entity.getType());
                }
            } catch (BeanInstantiationException e) {
                throw new MappingInstantiationException(entity, Collections.emptyList(), e);
            }
        }

        List<Object> params = new ArrayList<Object>();
        if (null != provider && constructor.hasParameters()) {
            for (Parameter<?, P> parameter : constructor.getParameters()) {
                params.add(provider.getParameterValue(parameter));
            }
        }

        try {
            return BeanUtils.instantiateClass(constructor.getConstructor(), params.toArray());
        } catch (BeanInstantiationException e) {
            throw new MappingInstantiationException(entity, params, e);
        }
    }
}

