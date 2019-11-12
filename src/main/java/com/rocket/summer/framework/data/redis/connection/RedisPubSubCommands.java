package com.rocket.summer.framework.data.redis.connection;

/**
 * PubSub-specific Redis commands.
 *
 * @author Costin Leau
 * @author Mark Paluch
 */
public interface RedisPubSubCommands {

    /**
     * Indicates whether the current connection is subscribed (to at least one channel) or not.
     *
     * @return true if the connection is subscribed, false otherwise
     */
    boolean isSubscribed();

    /**
     * Returns the current subscription for this connection or null if the connection is not subscribed.
     *
     * @return the current subscription, null if none is available
     */
    Subscription getSubscription();

    /**
     * Publishes the given message to the given channel.
     *
     * @param channel the channel to publish to, must not be {@literal null}.
     * @param message message to publish
     * @return the number of clients that received the message
     * @see <a href="http://redis.io/commands/publish">Redis Documentation: PUBLISH</a>
     */
    Long publish(byte[] channel, byte[] message);

    /**
     * Subscribes the connection to the given channels. Once subscribed, a connection enters listening mode and can only
     * subscribe to other channels or unsubscribe. No other commands are accepted until the connection is unsubscribed.
     * <p>
     * Note that this operation is blocking and the current thread starts waiting for new messages immediately.
     *
     * @param listener message listener, must not be {@literal null}.
     * @param channels channel names, must not be {@literal null}.
     * @see <a href="http://redis.io/commands/subscribe">Redis Documentation: SUBSCRIBE</a>
     */
    void subscribe(MessageListener listener, byte[]... channels);

    /**
     * Subscribes the connection to all channels matching the given patterns. Once subscribed, a connection enters
     * listening mode and can only subscribe to other channels or unsubscribe. No other commands are accepted until the
     * connection is unsubscribed.
     * <p>
     * Note that this operation is blocking and the current thread starts waiting for new messages immediately.
     *
     * @param listener message listener, must not be {@literal null}.
     * @param patterns channel name patterns, must not be {@literal null}.
     * @see <a href="http://redis.io/commands/psubscribe">Redis Documentation: PSUBSCRIBE</a>
     */
    void pSubscribe(MessageListener listener, byte[]... patterns);
}

