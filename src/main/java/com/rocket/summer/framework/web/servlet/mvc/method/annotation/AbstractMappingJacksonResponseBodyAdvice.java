package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.converter.json.AbstractJackson2HttpMessageConverter;
import com.rocket.summer.framework.http.converter.json.MappingJacksonValue;
import com.rocket.summer.framework.http.server.ServerHttpRequest;
import com.rocket.summer.framework.http.server.ServerHttpResponse;

/**
 * A convenient base class for {@code ResponseBodyAdvice} implementations
 * that customize the response before JSON serialization with
 * {@link AbstractJackson2HttpMessageConverter}'s concrete subclasses.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @since 4.1
 */
public abstract class AbstractMappingJacksonResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public final Object beforeBodyWrite(Object body, MethodParameter returnType,
                                        MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType,
                                        ServerHttpRequest request, ServerHttpResponse response) {

        MappingJacksonValue container = getOrCreateContainer(body);
        beforeBodyWriteInternal(container, contentType, returnType, request, response);
        return container;
    }

    /**
     * Wrap the body in a {@link MappingJacksonValue} value container (for providing
     * additional serialization instructions) or simply cast it if already wrapped.
     */
    protected MappingJacksonValue getOrCreateContainer(Object body) {
        return (body instanceof MappingJacksonValue ? (MappingJacksonValue) body : new MappingJacksonValue(body));
    }

    /**
     * Invoked only if the converter type is {@code MappingJackson2HttpMessageConverter}.
     */
    protected abstract void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
                                                    MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response);

}

