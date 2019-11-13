package com.rocket.summer.framework.data.redis.connection.jedis;

import com.rocket.summer.framework.data.redis.connection.DefaultMessage;
import com.rocket.summer.framework.data.redis.connection.MessageListener;
import com.rocket.summer.framework.util.Assert;

import redis.clients.jedis.BinaryJedisPubSub;

/**
 * MessageListener adapter on top of Jedis.
 *
 * @author Costin Leau
 */
class JedisMessageListener extends BinaryJedisPubSub {

    private final MessageListener listener;

    JedisMessageListener(MessageListener listener) {
        Assert.notNull(listener, "message listener is required");
        this.listener = listener;
    }

    public void onMessage(byte[] channel, byte[] message) {
        listener.onMessage(new DefaultMessage(channel, message), null);
    }

    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
        listener.onMessage(new DefaultMessage(channel, message), pattern);
    }

    public void onPSubscribe(byte[] pattern, int subscribedChannels) {
        // no-op
    }

    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
        // no-op
    }

    public void onSubscribe(byte[] channel, int subscribedChannels) {
        // no-op
    }

    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        // no-op
    }
}

