package com.rocket.summer.framework.data.repository.core;

import java.lang.reflect.Method;

/**
 * Aditional repository specific information
 *
 * @author Oliver Gierke
 */
public interface RepositoryInformation extends RepositoryMetadata {

    /**
     * Returns the base class to be used to create the proxy backing instance.
     *
     * @return
     */
    Class<?> getRepositoryBaseClass();

    /**
     * Returns if the configured repository interface has custom methods, that might have to be delegated to a custom
     * implementation. This is used to verify repository configuration.
     *
     * @return
     */
    boolean hasCustomMethod();

    /**
     * Returns whether the given method is a custom repository method.
     *
     * @param method
     * @return
     */
    boolean isCustomMethod(Method method);

    /**
     * Returns whether the given method is a query method.
     *
     * @param method
     * @return
     */
    boolean isQueryMethod(Method method);

    /**
     * Returns whether the given method is logically a base class method. This also includes methods (re)declared in the
     * repository interface that match the signatures of the base implementation.
     *
     * @param method must not be {@literal null}.
     * @return
     */
    boolean isBaseClassMethod(Method method);

    /**
     * Returns all methods considered to be query methods.
     *
     * @return
     */
    Iterable<Method> getQueryMethods();

    /**
     * Returns the target class method that is backing the given method. This can be necessary if a repository interface
     * redeclares a method of the core repository interface (e.g. for transaction behaviour customization). Returns the
     * method itself if the target class does not implement the given method. Implementations need to make sure the
     * {@link Method} returned can be invoked via reflection, i.e. needs to be accessible.
     *
     * @param method must not be {@literal null}.
     * @return
     */
    Method getTargetClassMethod(Method method);
}

