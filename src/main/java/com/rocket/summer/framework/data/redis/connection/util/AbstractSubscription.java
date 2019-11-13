package com.rocket.summer.framework.data.redis.connection.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rocket.summer.framework.data.redis.connection.MessageListener;
import com.rocket.summer.framework.data.redis.connection.RedisInvalidSubscriptionException;
import com.rocket.summer.framework.data.redis.connection.Subscription;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Base implementation for a subscription handling the channel/pattern registration so subclasses only have to deal with
 * the actual registration/unregistration.
 *
 * @author Costin Leau
 */
public abstract class AbstractSubscription implements Subscription {

    private final Collection<ByteArrayWrapper> channels = new ArrayList<ByteArrayWrapper>(2);
    private final Collection<ByteArrayWrapper> patterns = new ArrayList<ByteArrayWrapper>(2);
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final MessageListener listener;

    protected AbstractSubscription(MessageListener listener) {
        this(listener, null, null);
    }

    /**
     * Constructs a new <code>AbstractSubscription</code> instance. Allows channels and patterns to be added to the
     * subscription w/o triggering a subscription action (as some clients (Jedis) require an initial call before entering
     * into listening mode).
     *
     * @param listener
     * @param channels
     * @param patterns
     */
    protected AbstractSubscription(MessageListener listener, byte[][] channels, byte[][] patterns) {
        Assert.notNull(listener, "MessageListener must not be null!");
        this.listener = listener;

        synchronized (this.channels) {
            add(this.channels, channels);
        }
        synchronized (this.patterns) {
            add(this.patterns, patterns);
        }
    }

    /**
     * Subscribe to the given channels.
     *
     * @param channels channels to subscribe to
     */
    protected abstract void doSubscribe(byte[]... channels);

    /**
     * Channel unsubscribe.
     *
     * @param all true if all the channels are unsubscribed (used as a hint for the underlying implementation).
     * @param channels channels to be unsubscribed
     */
    protected abstract void doUnsubscribe(boolean all, byte[]... channels);

    /**
     * Subscribe to the given patterns
     *
     * @param patterns patterns to subscribe to
     */
    protected abstract void doPsubscribe(byte[]... patterns);

    /**
     * Pattern unsubscribe.
     *
     * @param all true if all the patterns are unsubscribed (used as a hint for the underlying implementation).
     * @param patterns patterns to be unsubscribed
     */
    protected abstract void doPUnsubscribe(boolean all, byte[]... patterns);

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.Subscription#close()
     */
    @Override
    public void close() {
        doClose();
    }

    /**
     * Shutdown the subscription and free any resources held.
     */
    protected abstract void doClose();

    public MessageListener getListener() {
        return listener;
    }

    public Collection<byte[]> getChannels() {
        synchronized (channels) {
            return clone(channels);
        }
    }

    public Collection<byte[]> getPatterns() {
        synchronized (patterns) {
            return clone(patterns);
        }
    }

    public void pSubscribe(byte[]... patterns) {
        checkPulse();

        Assert.notEmpty(patterns, "at least one pattern required");

        synchronized (this.patterns) {
            add(this.patterns, patterns);
        }

        doPsubscribe(patterns);
    }

    public void pUnsubscribe() {
        pUnsubscribe((byte[][]) null);
    }

    public void subscribe(byte[]... channels) {
        checkPulse();

        Assert.notEmpty(channels, "at least one channel required");

        synchronized (this.channels) {
            add(this.channels, channels);
        }

        doSubscribe(channels);
    }

    public void unsubscribe() {
        unsubscribe((byte[][]) null);
    }

    public void pUnsubscribe(byte[]... patts) {
        if (!isAlive()) {
            return;
        }

        // shortcut for unsubscribing all patterns
        if (ObjectUtils.isEmpty(patts)) {
            if (!this.patterns.isEmpty()) {
                synchronized (this.patterns) {
                    patts = getPatterns().toArray(new byte[this.patterns.size()][]);
                    doPUnsubscribe(true, patts);
                    this.patterns.clear();
                }
            } else {
                // nothing to unsubscribe from
                return;
            }
        } else {
            doPUnsubscribe(false, patts);
            synchronized (this.patterns) {
                remove(this.patterns, patts);
            }
        }

        closeIfUnsubscribed();
    }

    public void unsubscribe(byte[]... chans) {
        if (!isAlive()) {
            return;
        }

        // shortcut for unsubscribing all channels
        if (ObjectUtils.isEmpty(chans)) {
            if (!this.channels.isEmpty()) {
                synchronized (this.channels) {
                    chans = getChannels().toArray(new byte[this.channels.size()][]);
                    doUnsubscribe(true, chans);
                    this.channels.clear();
                }
            } else {
                // nothing to unsubscribe from
                return;
            }
        } else {
            doUnsubscribe(false, chans);
            synchronized (this.channels) {
                remove(this.channels, chans);
            }
        }

        closeIfUnsubscribed();
    }

    public boolean isAlive() {
        return alive.get();
    }

    private void checkPulse() {
        if (!isAlive()) {
            throw new RedisInvalidSubscriptionException("Subscription has been unsubscribed and cannot be used anymore");
        }
    }

    private void closeIfUnsubscribed() {
        if (channels.isEmpty() && patterns.isEmpty()) {
            alive.set(false);
            doClose();
        }
    }

    private static Collection<byte[]> clone(Collection<ByteArrayWrapper> col) {
        Collection<byte[]> list = new ArrayList<byte[]>(col.size());
        for (ByteArrayWrapper wrapper : col) {
            list.add(wrapper.getArray().clone());
        }
        return list;
    }

    private static void add(Collection<ByteArrayWrapper> col, byte[]... bytes) {
        if (!ObjectUtils.isEmpty(bytes)) {
            for (byte[] bs : bytes) {
                col.add(new ByteArrayWrapper(bs));
            }
        }
    }

    private static void remove(Collection<ByteArrayWrapper> col, byte[]... bytes) {
        if (!ObjectUtils.isEmpty(bytes)) {
            for (byte[] bs : bytes) {
                col.remove(new ByteArrayWrapper(bs));
            }
        }
    }
}

