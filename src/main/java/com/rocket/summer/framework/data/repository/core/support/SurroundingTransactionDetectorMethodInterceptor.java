package com.rocket.summer.framework.data.repository.core.support;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.rocket.summer.framework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link MethodInterceptor} detecting whether a transaction is already running and exposing that fact via
 * {@link #isSurroundingTransactionActive()}. Useful in case subsequent interceptors might create transactions
 * themselves but downstream components have to find out whether there was one running before the call entered the
 * proxy.
 *
 * @author Oliver Gierke
 * @since 1.13
 * @soundtrack Hendrik Freischlader Trio - Openness (Openness)
 */
public enum SurroundingTransactionDetectorMethodInterceptor implements MethodInterceptor {

    INSTANCE;

    private final ThreadLocal<Boolean> SURROUNDING_TX_ACTIVE = new ThreadLocal<Boolean>();

    /**
     * Returns whether a transaction was active before the method call entered the repository proxy.
     *
     * @return
     */
    public boolean isSurroundingTransactionActive() {
        return Boolean.TRUE == SURROUNDING_TX_ACTIVE.get();
    }

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        SURROUNDING_TX_ACTIVE.set(TransactionSynchronizationManager.isActualTransactionActive());

        try {
            return invocation.proceed();
        } finally {
            SURROUNDING_TX_ACTIVE.remove();
        }
    }
}

