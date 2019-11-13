package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.dao.DataAccessResourceFailureException;

/**
 * {@link DataAccessResourceFailureException} indicating the current local snapshot of cluster state does no longer
 * represent the actual remote state. This can happen nodes are removed from cluster, slots get migrated to other nodes
 * and so on.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class ClusterStateFailureException extends DataAccessResourceFailureException {

    private static final long serialVersionUID = 333399051713240852L;

    /**
     * Creates new {@link ClusterStateFailureException}.
     *
     * @param msg
     */
    public ClusterStateFailureException(String msg) {
        super(msg);
    }

    /**
     * Creates new {@link ClusterStateFailureException}.
     *
     * @param msg
     * @param cause
     */
    public ClusterStateFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

