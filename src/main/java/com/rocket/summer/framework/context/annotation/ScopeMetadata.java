package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;

/**
 * Describes scope characteristics for a Spring-managed bean including the scope
 * name and the scoped-proxy behavior.
 *
 * <p>The default scope is "singleton", and the default is to <i>not</i> create
 * scoped-proxies.
 *
 * @author Mark Fisher
 * @since 2.5
 * @see ScopeMetadataResolver
 * @see ScopedProxyMode
 */
public class ScopeMetadata {

    private String scopeName = BeanDefinition.SCOPE_SINGLETON;

    private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;


    /**
     * Get the name of the scope.
     * @return said scope name
     */
    public String getScopeName() {
        return scopeName;
    }

    /**
     * Set the name of the scope.
     * @param scopeName said scope name
     */
    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    /**
     * Get the proxy-mode to be applied to the scoped instance.
     * @return said scoped-proxy mode
     */
    public ScopedProxyMode getScopedProxyMode() {
        return scopedProxyMode;
    }

    /**
     * Set the proxy-mode to be applied to the scoped instance.
     * @param scopedProxyMode said scoped-proxy mode
     */
    public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
        this.scopedProxyMode = scopedProxyMode;
    }

}

