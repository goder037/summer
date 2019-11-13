package com.rocket.summer.framework.transaction.support;

import com.rocket.summer.framework.core.Ordered;

/**
 * Simple {@link TransactionSynchronization} adapter containing empty
 * method implementations, for easier overriding of single methods.
 *
 * <p>Also implements the {@link Ordered} interface to enable the execution
 * order of synchronizations to be controlled declaratively. The default
 * {@link #getOrder() order} is {@link Ordered#LOWEST_PRECEDENCE}, indicating
 * late execution; return a lower value for earlier execution.
 *
 * @author Juergen Hoeller
 * @since 22.01.2004
 */
public abstract class TransactionSynchronizationAdapter implements TransactionSynchronization, Ordered {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void beforeCommit(boolean readOnly) {
    }

    @Override
    public void beforeCompletion() {
    }

    @Override
    public void afterCommit() {
    }

    @Override
    public void afterCompletion(int status) {
    }

}

