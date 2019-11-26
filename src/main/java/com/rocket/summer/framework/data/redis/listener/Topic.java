package com.rocket.summer.framework.data.redis.listener;

/**
 * Topic for a Redis message. Acts a high-level abstraction on top of Redis low-level channels or patterns.
 *
 * @author Costin Leau
 */
public interface Topic {

    /**
     * Returns the topic (as a String).
     *
     * @return the topic
     */
    String getTopic();
}
