package com.rocket.summer.framework.cache.transaction;

import java.util.concurrent.Callable;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.transaction.support.TransactionSynchronizationAdapter;
import com.rocket.summer.framework.transaction.support.TransactionSynchronizationManager;
import com.rocket.summer.framework.util.Assert;

/**
 * Cache decorator which synchronizes its {@link #put}, {@link #evict} and {@link #clear}
 * operations with Spring-managed transactions (through Spring's {@link TransactionSynchronizationManager},
 * performing the actual cache put/evict/clear operation only in the after-commit phase of a
 * successful transaction. If no transaction is active, {@link #put}, {@link #evict} and
 * {@link #clear} operations will be performed immediately, as usual.
 *
 * <p>Use of more aggressive operations such as {@link #putIfAbsent} cannot be deferred
 * to the after-commit phase of a running transaction. Use these with care.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @author Stas Volsky
 * @since 3.2
 * @see TransactionAwareCacheManagerProxy
 */
public class TransactionAwareCacheDecorator implements Cache {

    private final Cache targetCache;


    /**
     * Create a new TransactionAwareCache for the given target Cache.
     * @param targetCache the target Cache to decorate
     */
    public TransactionAwareCacheDecorator(Cache targetCache) {
        Assert.notNull(targetCache, "Target Cache must not be null");
        this.targetCache = targetCache;
    }

    /**
     * Return the target Cache that this Cache should delegate to.
     */
    public Cache getTargetCache() {
        return this.targetCache;
    }

    @Override
    public String getName() {
        return this.targetCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return this.targetCache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return this.targetCache.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return this.targetCache.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return this.targetCache.get(key, valueLoader);
    }

    @Override
    public void put(final Object key, final Object value) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.put(key, value);
                }
            });
        }
        else {
            this.targetCache.put(key, value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(final Object key, final Object value) {
        return this.targetCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(final Object key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.evict(key);
                }
            });
        }
        else {
            this.targetCache.evict(key);
        }
    }

    @Override
    public void clear() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    targetCache.clear();
                }
            });
        }
        else {
            this.targetCache.clear();
        }
    }

}

