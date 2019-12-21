package com.rocket.summer.framework.boot.context.embedded;

/**
 * Simple container-independent abstraction for SSL configuration.
 *
 * @author Andy Wilkinson
 * @author Vladimir Tsanev
 * @since 1.1.7
 */
public class Ssl {

    /**
     * Enable SSL support.
     */
    private boolean enabled = true;

    /**
     * Whether client authentication is wanted ("want") or needed ("need"). Requires a
     * trust store.
     */
    private ClientAuth clientAuth;

    /**
     * Supported SSL ciphers.
     */
    private String[] ciphers;

    /**
     * Enabled SSL protocols.
     */
    private String[] enabledProtocols;

    /**
     * Alias that identifies the key in the key store.
     */
    private String keyAlias;

    /**
     * Password used to access the key in the key store.
     */
    private String keyPassword;

    /**
     * Path to the key store that holds the SSL certificate (typically a jks file).
     */
    private String keyStore;

    /**
     * Password used to access the key store.
     */
    private String keyStorePassword;

    /**
     * Type of the key store.
     */
    private String keyStoreType;

    /**
     * Provider for the key store.
     */
    private String keyStoreProvider;

    /**
     * Trust store that holds SSL certificates.
     */
    private String trustStore;

    /**
     * Password used to access the trust store.
     */
    private String trustStorePassword;

    /**
     * Type of the trust store.
     */
    private String trustStoreType;

    /**
     * Provider for the trust store.
     */
    private String trustStoreProvider;

    /**
     * SSL protocol to use.
     */
    private String protocol = "TLS";

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ClientAuth getClientAuth() {
        return this.clientAuth;
    }

    public void setClientAuth(ClientAuth clientAuth) {
        this.clientAuth = clientAuth;
    }

    public String[] getCiphers() {
        return this.ciphers;
    }

    public void setCiphers(String[] ciphers) {
        this.ciphers = ciphers;
    }

    public String getKeyAlias() {
        return this.keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPassword() {
        return this.keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyStore() {
        return this.keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStoreProvider() {
        return this.keyStoreProvider;
    }

    public void setKeyStoreProvider(String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
    }

    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    public void setEnabledProtocols(String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    public String getTrustStore() {
        return this.trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return this.trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getTrustStoreType() {
        return this.trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getTrustStoreProvider() {
        return this.trustStoreProvider;
    }

    public void setTrustStoreProvider(String trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Client authentication types.
     */
    public enum ClientAuth {

        /**
         * Client authentication is wanted but not mandatory.
         */
        WANT,

        /**
         * Client authentication is needed and mandatory.
         */
        NEED

    }

}
