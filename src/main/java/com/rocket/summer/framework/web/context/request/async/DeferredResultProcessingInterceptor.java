package com.rocket.summer.framework.web.context.request.async;

import com.rocket.summer.framework.web.context.request.NativeWebRequest;

/**
 * Intercepts concurrent request handling, where the concurrent result is
 * obtained by waiting for a {@link DeferredResult} to be set from a thread
 * chosen by the application (e.g. in response to some external event).
 *
 * <p>A {@code DeferredResultProcessingInterceptor} is invoked before the start
 * of async processing, after the {@code DeferredResult} is set as well as on
 * timeout, or after completing for any reason including a timeout or network
 * error.
 *
 * <p>As a general rule exceptions raised by interceptor methods will cause
 * async processing to resume by dispatching back to the container and using
 * the Exception instance as the concurrent result. Such exceptions will then
 * be processed through the {@code HandlerExceptionResolver} mechanism.
 *
 * <p>The {@link #handleTimeout(NativeWebRequest, DeferredResult) afterTimeout}
 * method can set the {@code DeferredResult} in order to resume processing.
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
public interface DeferredResultProcessingInterceptor {

    /**
     * Invoked immediately before the start of concurrent handling, in the same
     * thread that started it. This method may be used to capture state just prior
     * to the start of concurrent processing with the given {@code DeferredResult}.
     * @param request the current request
     * @param deferredResult the DeferredResult for the current request
     * @throws Exception in case of errors
     */
    <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

    /**
     * Invoked immediately after the start of concurrent handling, in the same
     * thread that started it. This method may be used to detect the start of
     * concurrent processing with the given {@code DeferredResult}.
     * <p>The {@code DeferredResult} may have already been set, for example at
     * the time of its creation or by another thread.
     * @param request the current request
     * @param deferredResult the DeferredResult for the current request
     * @throws Exception in case of errors
     */
    <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

    /**
     * Invoked after a {@code DeferredResult} has been set, via
     * {@link DeferredResult#setResult(Object)} or
     * {@link DeferredResult#setErrorResult(Object)}, and is also ready to
     * handle the concurrent result.
     * <p>This method may also be invoked after a timeout when the
     * {@code DeferredResult} was created with a constructor accepting a default
     * timeout result.
     * @param request the current request
     * @param deferredResult the DeferredResult for the current request
     * @param concurrentResult the result to which the {@code DeferredResult}
     * @throws Exception in case of errors
     */
    <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult) throws Exception;

    /**
     * Invoked from a container thread when an async request times out before
     * the {@code DeferredResult} has been set. Implementations may invoke
     * {@link DeferredResult#setResult(Object) setResult} or
     * {@link DeferredResult#setErrorResult(Object) setErrorResult} to resume processing.
     * @param request the current request
     * @param deferredResult the DeferredResult for the current request; if the
     * {@code DeferredResult} is set, then concurrent processing is resumed and
     * subsequent interceptors are not invoked
     * @return {@code true} if processing should continue, or {@code false} if
     * other interceptors should not be invoked
     * @throws Exception in case of errors
     */
    <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

    /**
     * Invoked from a container thread when an async request completed for any
     * reason including timeout and network error. This method is useful for
     * detecting that a {@code DeferredResult} instance is no longer usable.
     * @param request the current request
     * @param deferredResult the DeferredResult for the current request
     * @throws Exception in case of errors
     */
    <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

}

