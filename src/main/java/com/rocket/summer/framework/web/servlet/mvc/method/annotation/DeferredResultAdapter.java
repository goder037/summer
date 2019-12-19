package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.web.context.request.async.DeferredResult;

/**
 * Contract to adapt a single-value async return value to {@code DeferredResult}.
 *
 * @author Rossen Stoyanchev
 * @since 4.3
 *
 * @deprecated in 4.3.8 since 5.0 adds first-class support for reactive type
 * return values from Spring MVC controller methods based on the pluggable
 * {@code ReactiveAdapterRegistry} mechanism. Yet another alternative would
 * be to implement a custom
 * {@link com.rocket.summer.framework.web.method.support.AsyncHandlerMethodReturnValueHandler}.
 */
@Deprecated
public interface DeferredResultAdapter {

    /**
     * Create a {@code DeferredResult} for the given return value.
     * @param returnValue the return value (never {@code null})
     * @return the DeferredResult
     */
    DeferredResult<?> adaptToDeferredResult(Object returnValue);

}
