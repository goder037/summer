package com.rocket.summer.framework.util.concurrent;

/**
 * Callback mechanism for the outcome, success or failure, from a
 * {@link ListenableFuture}.
 *
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 * @since 4.0
 */
public interface ListenableFutureCallback<T> extends SuccessCallback<T>, FailureCallback {

}
