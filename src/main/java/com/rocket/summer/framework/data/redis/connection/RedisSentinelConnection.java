package com.rocket.summer.framework.data.redis.connection;

import java.io.Closeable;

/**
 * @author Christoph Strobl
 * @since 1.4
 */
public interface RedisSentinelConnection extends RedisSentinelCommands, Closeable {

    /**
     * @return true if connected to server
     */
    boolean isOpen();

}
