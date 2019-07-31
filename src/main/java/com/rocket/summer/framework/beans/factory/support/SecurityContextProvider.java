package com.rocket.summer.framework.beans.factory.support;

import java.security.AccessControlContext;

/**
 * Provider of the security context of the code running inside the bean factory.
 *
 * @author Costin Leau
 * @since 3.0
 */
public interface SecurityContextProvider {

    /**
     * Provides a security access control context relevant to a bean factory.
     * @return bean factory security control context
     */
    AccessControlContext getAccessControlContext();

}
