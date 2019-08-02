package com.rocket.summer.framework.util.concurrent;

/**
 * Success callback for a {@link ListenableFuture}.
 *
 * @author Sebastien Deleuze
 * @since 4.1
 */
public interface SuccessCallback<T> {

    /**
     * Called when the {@link ListenableFuture} completes with success.
     * <p>Note that Exceptions raised by this method are ignored.
     * @param result the result
     */
    void onSuccess(T result);

}
