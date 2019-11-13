package com.rocket.summer.framework.data.redis.connection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.dao.UncategorizedDataAccessException;

/**
 * Exception thrown when at least one call to a clustered redis environment fails.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class ClusterCommandExecutionFailureException extends UncategorizedDataAccessException {

    private static final long serialVersionUID = 5727044227040368955L;

    private final Collection<? extends Throwable> causes;

    /**
     * Creates new {@link ClusterCommandExecutionFailureException}.
     *
     * @param cause must not be {@literal null}.
     */
    public ClusterCommandExecutionFailureException(Throwable cause) {
        this(Collections.singletonList(cause));
    }

    /**
     * Creates new {@link ClusterCommandExecutionFailureException}.
     *
     * @param causes must not be {@literal empty}.
     */
    public ClusterCommandExecutionFailureException(List<? extends Throwable> causes) {

        super(causes.get(0).getMessage(), causes.get(0));
        this.causes = causes;
    }

    /**
     * Get the collected errors.
     *
     * @return never {@literal null}.
     */
    public Collection<? extends Throwable> getCauses() {
        return causes;
    }
}

