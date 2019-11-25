package com.rocket.summer.framework.data.repository.core.support;

import com.rocket.summer.framework.aop.framework.ProxyFactory;
import com.rocket.summer.framework.data.repository.core.RepositoryInformation;

/**
 * Callback interface used during repository proxy creation. Allows manipulating the {@link ProxyFactory} creating the
 * repository.
 *
 * @author Oliver Gierke
 */
public interface RepositoryProxyPostProcessor {

    /**
     * Manipulates the {@link ProxyFactory}, e.g. add further interceptors to it.
     *
     * @param factory will never be {@literal null}.
     * @param repositoryInformation will never be {@literal null}.
     */
    void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation);
}

