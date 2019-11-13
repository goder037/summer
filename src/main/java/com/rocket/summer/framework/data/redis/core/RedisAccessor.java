package com.rocket.summer.framework.data.redis.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.data.redis.connection.RedisConnectionFactory;
import com.rocket.summer.framework.util.Assert;

/**
 * Base class for {@link RedisTemplate} defining common properties. Not intended to be used directly.
 *
 * @author Costin Leau
 */
public class RedisAccessor implements InitializingBean {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private RedisConnectionFactory connectionFactory;

    public void afterPropertiesSet() {
        Assert.notNull(getConnectionFactory(), "RedisConnectionFactory is required");
    }

    /**
     * Returns the connectionFactory.
     *
     * @return Returns the connectionFactory
     */
    public RedisConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * Sets the connection factory.
     *
     * @param connectionFactory The connectionFactory to set.
     */
    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}

