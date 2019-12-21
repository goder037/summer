package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.http.server.ServerHttpResponse;

/**
 * Contract to adapt streaming async types to {@code ResponseBodyEmitter}.
 *
 * @author Rossen Stoyanchev
 * @since 4.3
 *
 * @deprecated in 4.3.8 since 5.0 adds first-class support for reactive type
 * return values from Spring MVC controller methods based on the pluggable
 * {@code ReactiveAdapterRegistry} mechanism.
 */
@Deprecated
public interface ResponseBodyEmitterAdapter {

    /**
     * Obtain a {@code ResponseBodyEmitter} for the given return value.
     * If the return is the body {@code ResponseEntity} then the given
     * {@code ServerHttpResponse} contains its status and headers.
     * @param returnValue the return value (never {@code null})
     * @param response the response
     * @return the return value adapted to a {@code ResponseBodyEmitter}
     */
    ResponseBodyEmitter adaptToEmitter(Object returnValue, ServerHttpResponse response);

}

