package com.rocket.summer.framework.data.redis.connection;

/**
 * Connection-specific commands supported by Redis.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public interface RedisConnectionCommands {

    /**
     * Select the DB with given positive {@code dbIndex}.
     *
     * @param dbIndex the database index.
     * @see <a href="http://redis.io/commands/select">Redis Documentation: SELECT</a>
     */
    void select(int dbIndex);

    /**
     * Returns {@code message} via server roundtrip.
     *
     * @param message the message to echo.
     * @return
     * @see <a href="http://redis.io/commands/echo">Redis Documentation: ECHO</a>
     */
    byte[] echo(byte[] message);

    /**
     * Test connection.
     *
     * @return Server response message - usually {@literal PONG}.
     * @see <a href="http://redis.io/commands/ping">Redis Documentation: PING</a>
     */
    String ping();
}
