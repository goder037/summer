package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.dao.DataAccessResourceFailureException;

/**
 * Fatal exception thrown when the Redis connection fails completely.
 *
 * @author Mark Pollack
 */
public class RedisConnectionFailureException extends DataAccessResourceFailureException {

    public RedisConnectionFailureException(String msg) {
        super(msg);
    }

    public RedisConnectionFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
