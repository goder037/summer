package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpEntity;
import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.ResponseEntity;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.server.ServletServerHttpRequest;
import com.rocket.summer.framework.http.server.ServletServerHttpResponse;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.HttpMediaTypeNotSupportedException;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Resolves {@link HttpEntity} method argument values and also handles
 * both {@link HttpEntity} and {@link ResponseEntity} return values.
 *
 * <p>An {@link HttpEntity} return type has a set purpose. Therefore this
 * handler should be configured ahead of handlers that support any return
 * value type annotated with {@code @ModelAttribute} or {@code @ResponseBody}
 * to ensure they don't take over.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class HttpEntityMethodProcessor extends AbstractMessageConverterMethodProcessor {

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return HttpEntity.class.equals(parameterType);
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> parameterType = returnType.getParameterType();
        return HttpEntity.class.isAssignableFrom(parameterType) || ResponseEntity.class.isAssignableFrom(parameterType);
    }

    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws IOException, HttpMediaTypeNotSupportedException {

        HttpInputMessage inputMessage = createInputMessage(webRequest);
        Class<?> paramType = getHttpEntityType(parameter);

        Object body = readWithMessageConverters(webRequest, parameter, paramType);
        return new HttpEntity<Object>(body, inputMessage.getHeaders());
    }

    private Class<?> getHttpEntityType(MethodParameter parameter) {
        Assert.isAssignable(HttpEntity.class, parameter.getParameterType());
        ParameterizedType type = (ParameterizedType) parameter.getGenericParameterType();
        if (type.getActualTypeArguments().length == 1) {
            Type typeArgument = type.getActualTypeArguments()[0];
            if (typeArgument instanceof Class) {
                return (Class<?>) typeArgument;
            }
            else if (typeArgument instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) typeArgument).getGenericComponentType();
                if (componentType instanceof Class) {
                    // Surely, there should be a nicer way to determine the array type
                    Object array = Array.newInstance((Class<?>) componentType, 0);
                    return array.getClass();
                }
            }
        }
        throw new IllegalArgumentException("HttpEntity parameter (" + parameter.getParameterName() + ") "
                + "in method " + parameter.getMethod() + "is not parameterized");
    }

    public void handleReturnValue(
            Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {

        mavContainer.setRequestHandled(true);

        if (returnValue == null) {
            return;
        }

        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

        Assert.isInstanceOf(HttpEntity.class, returnValue);
        HttpEntity<?> responseEntity = (HttpEntity<?>) returnValue;
        if (responseEntity instanceof ResponseEntity) {
            outputMessage.setStatusCode(((ResponseEntity<?>) responseEntity).getStatusCode());
        }

        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            outputMessage.getHeaders().putAll(entityHeaders);
        }

        Object body = responseEntity.getBody();
        if (body != null) {
            writeWithMessageConverters(body, returnType, inputMessage, outputMessage);
        }
        else {
            // flush headers to the HttpServletResponse
            outputMessage.getBody();
        }
    }

}
