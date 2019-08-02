package com.rocket.summer.framework.boot.context.embedded;

import java.security.KeyStore;

/**
 * Interface to provide SSL key stores for an {@link EmbeddedServletContainer} to use. Can
 * be used when file based key stores cannot be used.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public interface SslStoreProvider {

    /**
     * Return the key store that should be used.
     * @return the key store to use
     * @throws Exception on load error
     */
    KeyStore getKeyStore() throws Exception;

    /**
     * Return the trust store that should be used.
     * @return the trust store to use
     * @throws Exception on load error
     */
    KeyStore getTrustStore() throws Exception;

}

