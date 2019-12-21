package com.rocket.summer.framework.cache.transaction;

import com.rocket.summer.framework.cache.Cache;
import com.rocket.summer.framework.cache.support.AbstractCacheManager;

/**
 * Base class for CacheManager implementations that want to support built-in
 * awareness of Spring-managed transactions. This usually needs to be switched
 * on explicitly through the {@link #setTransactionAware} bean property.
 *
 * @author Juergen Hoeller
 * @since 3.2
 * @see #setTransactionAware
 * @see TransactionAwareCacheDecorator
 * @see TransactionAwareCacheManagerProxy
 */
public abstract class AbstractTransactionSupportingCacheManager extends AbstractCacheManager {

    private boolean transactionAware = false;


    /**
     * Set whether this CacheManager should expose transaction-aware Cache objects.
     * <p>Default is "false". Set this to "true" to synchronize cache put/evict
     * operations with ongoing Spring-managed transactions, performing the actual cache
     * put/evict operation only in the after-commit phase of a successful transaction.
     */
    public void setTransactionAware(boolean transactionAware) {
        this.transactionAware = transactionAware;
    }

    /**
     * Return whether this CacheManager has been configured to be transaction-aware.
     */
    public boolean isTransactionAware() {
        return this.transactionAware;
    }


    @Override
    protected Cache decorateCache(Cache cache) {
        return (isTransactionAware() ? new TransactionAwareCacheDecorator(cache) : cache);
    }

}

