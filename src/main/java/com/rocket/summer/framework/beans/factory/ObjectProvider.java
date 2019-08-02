package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.context.BeansException;

/**
 * A variant of {@link ObjectFactory} designed specifically for injection points,
 * allowing for programmatic optionality and lenient not-unique handling.
 *
 * @author Juergen Hoeller
 * @since 4.3
 */
public interface ObjectProvider<T> extends ObjectFactory<T> {

    /**
     * Return an instance (possibly shared or independent) of the object
     * managed by this factory.
     * <p>Allows for specifying explicit construction arguments, along the
     * lines of {@link BeanFactory#getBean(String, Object...)}.
     * @param args arguments to use when creating a corresponding instance
     * @return an instance of the bean
     * @throws BeansException in case of creation errors
     * @see #getObject()
     */
    T getObject(Object... args) throws BeansException;

    /**
     * Return an instance (possibly shared or independent) of the object
     * managed by this factory.
     * @return an instance of the bean, or {@code null} if not available
     * @throws BeansException in case of creation errors
     * @see #getObject()
     */
    T getIfAvailable() throws BeansException;

    /**
     * Return an instance (possibly shared or independent) of the object
     * managed by this factory.
     * @return an instance of the bean, or {@code null} if not available or
     * not unique (i.e. multiple candidates found with none marked as primary)
     * @throws BeansException in case of creation errors
     * @see #getObject()
     */
    T getIfUnique() throws BeansException;

}

