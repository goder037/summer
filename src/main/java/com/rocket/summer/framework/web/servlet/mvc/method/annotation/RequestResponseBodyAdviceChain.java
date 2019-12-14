package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.server.ServerHttpRequest;
import com.rocket.summer.framework.http.server.ServerHttpResponse;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.web.method.ControllerAdviceBean;

/**
 * Invokes {@link RequestBodyAdvice} and {@link ResponseBodyAdvice} where each
 * instance may be (and is most likely) wrapped with
 * {@link com.rocket.summer.framework.web.method.ControllerAdviceBean ControllerAdviceBean}.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.2
 */
class RequestResponseBodyAdviceChain implements RequestBodyAdvice, ResponseBodyAdvice<Object> {

    private final List<Object> requestBodyAdvice = new ArrayList<Object>(4);

    private final List<Object> responseBodyAdvice = new ArrayList<Object>(4);


    /**
     * Create an instance from a list of objects that are either of type
     * {@code RequestBodyAdvice} or {@code ResponseBodyAdvice}.
     */
    public RequestResponseBodyAdviceChain(List<Object> requestResponseBodyAdvice) {
        initAdvice(requestResponseBodyAdvice);
    }

    private void initAdvice(List<Object> requestResponseBodyAdvice) {
        if (requestResponseBodyAdvice == null) {
            return;
        }
        for (Object advice : requestResponseBodyAdvice) {
            Class<?> beanType = (advice instanceof ControllerAdviceBean ?
                    ((ControllerAdviceBean) advice).getBeanType() : advice.getClass());
            if (RequestBodyAdvice.class.isAssignableFrom(beanType)) {
                this.requestBodyAdvice.add(advice);
            }
            if (ResponseBodyAdvice.class.isAssignableFrom(beanType)) {
                this.responseBodyAdvice.add(advice);
            }
        }
    }

    private List<Object> getAdvice(Class<?> adviceType) {
        if (RequestBodyAdvice.class == adviceType) {
            return this.requestBodyAdvice;
        }
        else if (ResponseBodyAdvice.class == adviceType) {
            return this.responseBodyAdvice;
        }
        else {
            throw new IllegalArgumentException("Unexpected adviceType: " + adviceType);
        }
    }


    @Override
    public boolean supports(MethodParameter param, Type type, Class<? extends HttpMessageConverter<?>> converterType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (advice.supports(parameter, targetType, converterType)) {
                body = advice.handleEmptyBody(body, inputMessage, parameter, targetType, converterType);
            }
        }
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage request, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {

        for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (advice.supports(parameter, targetType, converterType)) {
                request = advice.beforeBodyRead(request, parameter, targetType, converterType);
            }
        }
        return request;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (advice.supports(parameter, targetType, converterType)) {
                body = advice.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
            }
        }
        return body;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        return processBody(body, returnType, contentType, converterType, request, response);
    }

    @SuppressWarnings("unchecked")
    private <T> Object processBody(Object body, MethodParameter returnType, MediaType contentType,
                                   Class<? extends HttpMessageConverter<?>> converterType,
                                   ServerHttpRequest request, ServerHttpResponse response) {

        for (ResponseBodyAdvice<?> advice : getMatchingAdvice(returnType, ResponseBodyAdvice.class)) {
            if (advice.supports(returnType, converterType)) {
                body = ((ResponseBodyAdvice<T>) advice).beforeBodyWrite((T) body, returnType,
                        contentType, converterType, request, response);
            }
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    private <A> List<A> getMatchingAdvice(MethodParameter parameter, Class<? extends A> adviceType) {
        List<Object> availableAdvice = getAdvice(adviceType);
        if (CollectionUtils.isEmpty(availableAdvice)) {
            return Collections.emptyList();
        }
        List<A> result = new ArrayList<A>(availableAdvice.size());
        for (Object advice : availableAdvice) {
            if (advice instanceof ControllerAdviceBean) {
                ControllerAdviceBean adviceBean = (ControllerAdviceBean) advice;
                if (!adviceBean.isApplicableToBeanType(parameter.getContainingClass())) {
                    continue;
                }
                advice = adviceBean.resolveBean();
            }
            if (adviceType.isAssignableFrom(advice.getClass())) {
                result.add((A) advice);
            }
        }
        return result;
    }

}

