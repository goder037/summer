package com.rocket.summer.framework.data.redis.connection.jedis;

import java.io.IOException;
import java.net.UnknownHostException;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.dao.InvalidDataAccessApiUsageException;
import com.rocket.summer.framework.data.redis.ClusterRedirectException;
import com.rocket.summer.framework.data.redis.RedisConnectionFailureException;
import com.rocket.summer.framework.data.redis.TooManyClusterRedirectionsException;

import redis.clients.jedis.exceptions.JedisClusterMaxRedirectionsException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisRedirectionException;

/**
 * Converts Exceptions thrown from Jedis to {@link DataAccessException}s
 *
 * @author Jennifer Hickey
 * @author Thomas Darimont
 * @author Christoph Strobl
 */
public class JedisExceptionConverter implements Converter<Exception, DataAccessException> {

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    public DataAccessException convert(Exception ex) {

        if (ex instanceof DataAccessException) {
            return (DataAccessException) ex;
        }
        if (ex instanceof JedisDataException) {

            if (ex instanceof JedisRedirectionException) {
                JedisRedirectionException re = (JedisRedirectionException) ex;
                return new ClusterRedirectException(re.getSlot(), re.getTargetNode().getHost(), re.getTargetNode().getPort(),
                        ex);
            }

            if (ex instanceof JedisClusterMaxRedirectionsException) {
                return new TooManyClusterRedirectionsException(ex.getMessage(), ex);
            }

            return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
        }
        if (ex instanceof JedisConnectionException) {
            return new RedisConnectionFailureException(ex.getMessage(), ex);
        }
        if (ex instanceof JedisException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
        }
        if (ex instanceof UnknownHostException) {
            return new RedisConnectionFailureException("Unknown host " + ex.getMessage(), ex);
        }
        if (ex instanceof IOException) {
            return new RedisConnectionFailureException("Could not connect to Redis server", ex);
        }

        return null;
    }
}

