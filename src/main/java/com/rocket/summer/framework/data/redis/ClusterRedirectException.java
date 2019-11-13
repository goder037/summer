package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.dao.DataRetrievalFailureException;

/**
 * {@link ClusterRedirectException} indicates that a requested slot is not served by the targeted server but can be
 * obtained on another one.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class ClusterRedirectException extends DataRetrievalFailureException {

    private static final long serialVersionUID = -857075813794333965L;

    private final int slot;
    private final String host;
    private final int port;

    /**
     * Creates new {@link ClusterRedirectException}.
     *
     * @param slot the slot to redirect to.
     * @param targetHost the host to redirect to.
     * @param targetPort the port on the host.
     * @param e the root cause from the data access API in use
     */
    public ClusterRedirectException(int slot, String targetHost, int targetPort, Throwable e) {

        super(String.format("Redirect: slot %s to %s:%s.", slot, targetHost, targetPort), e);

        this.slot = slot;
        this.host = targetHost;
        this.port = targetPort;
    }

    /**
     * Get slot to go for.
     *
     * @return
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Get host serving the slot.
     *
     * @return
     */
    public String getTargetHost() {
        return host;
    }

    /**
     * Get port on host serving the slot.
     *
     * @return
     */
    public int getTargetPort() {
        return port;
    }

}

