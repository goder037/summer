package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;

/**
 * A convenient starting point for implementing
 * {@link com.rocket.summer.framework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 * ResponseBodyAdvice} with default method implementations.
 *
 * <p>Sub-classes are required to implement {@link #supports} to return true
 * depending on when the advice applies.
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 */
public abstract class RequestBodyAdviceAdapter implements RequestBodyAdvice {

    /**
     * The default implementation returns the body that was passed in.
     */
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage,
                                  MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {

        return body;
    }

    /**
     * The default implementation returns the InputMessage that was passed in.
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
            throws IOException {

        return inputMessage;
    }

    /**
     * The default implementation returns the body that was passed in.
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        return body;
    }

}

