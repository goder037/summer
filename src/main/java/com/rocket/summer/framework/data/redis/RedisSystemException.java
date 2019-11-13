package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.dao.UncategorizedDataAccessException;

/**
 * Exception thrown when we can't classify a Redis exception into one of Spring generic data access exceptions.
 *
 * @author Costin Leau
 */
public class RedisSystemException extends UncategorizedDataAccessException {

    public RedisSystemException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

