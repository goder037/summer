package com.rocket.summer.framework.data.redis.listener;

import com.rocket.summer.framework.context.ApplicationEventPublisher;
import com.rocket.summer.framework.context.ApplicationEventPublisherAware;
import com.rocket.summer.framework.data.redis.connection.Message;
import com.rocket.summer.framework.data.redis.connection.MessageListener;
import com.rocket.summer.framework.data.redis.core.RedisKeyExpiredEvent;

/**
 * {@link MessageListener} publishing {@link RedisKeyExpiredEvent}s via {@link ApplicationEventPublisher} by listening
 * to Redis keyspace notifications for key expirations.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class KeyExpirationEventMessageListener extends KeyspaceEventMessageListener implements
        ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;
    private static final Topic KEYEVENT_EXPIRED_TOPIC = new PatternTopic("__keyevent@*__:expired");

    /**
     * Creates new {@link MessageListener} for {@code __keyevent@*__:expired} messages.
     *
     * @param listenerContainer must not be {@literal null}.
     */
    public KeyExpirationEventMessageListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.listener.KeyspaceEventMessageListener#doRegister(com.rocket.summer.framework.data.redis.listener.RedisMessageListenerContainer)
     */
    @Override
    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
        listenerContainer.addMessageListener(this, KEYEVENT_EXPIRED_TOPIC);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.listener.KeyspaceEventMessageListener#doHandleMessage(com.rocket.summer.framework.data.redis.connection.Message)
     */
    @Override
    protected void doHandleMessage(Message message) {
        publishEvent(new RedisKeyExpiredEvent(message.getBody()));
    }

    /**
     * Publish the event in case an {@link ApplicationEventPublisher} is set.
     *
     * @param event can be {@literal null}.
     */
    protected void publishEvent(RedisKeyExpiredEvent event) {

        if (publisher != null && event != null) {
            this.publisher.publishEvent(event);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(com.rocket.summer.framework.context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}

