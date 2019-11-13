package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.dao.DataRetrievalFailureException;

/**
 * {@link DataRetrievalFailureException} thrown when following cluster redirects exceeds the max number of edges.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class TooManyClusterRedirectionsException extends DataRetrievalFailureException {

    private static final long serialVersionUID = -2818933672669154328L;

    /**
     * Creates new {@link TooManyClusterRedirectionsException}.
     *
     * @param msg
     */
    public TooManyClusterRedirectionsException(String msg) {
        super(msg);
    }

    /**
     * Creates new {@link TooManyClusterRedirectionsException}.
     *
     * @param msg
     * @param cause
     */
    public TooManyClusterRedirectionsException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

