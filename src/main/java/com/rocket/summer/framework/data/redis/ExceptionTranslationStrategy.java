package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.dao.DataAccessException;

/**
 * Potentially translates an {@link Exception} into appropriate {@link DataAccessException}.
 *
 * @author Christoph Strobl
 * @author Thomas Darimont
 * @since 1.4
 */
public interface ExceptionTranslationStrategy {

    /**
     * Potentially translate the given {@link Exception} into {@link DataAccessException}.
     *
     * @param e
     * @return
     */
    DataAccessException translate(Exception e);

}
