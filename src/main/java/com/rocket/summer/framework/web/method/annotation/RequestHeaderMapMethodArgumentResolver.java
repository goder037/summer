package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.web.bind.annotation.RequestHeader;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resolves {@link Map} method arguments annotated with {@code @RequestHeader}.
 * For individual header values annotated with {@code @RequestHeader} see
 * {@link RequestHeaderMethodArgumentResolver} instead.
 *
 * <p>The created {@link Map} contains all request header name/value pairs.
 * The method parameter type may be a {@link MultiValueMap} to receive all
 * values for a header, not only the first one.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class RequestHeaderMapMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class)
                && Map.class.isAssignableFrom(parameter.getParameterType());
    }

    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        Class<?> paramType = parameter.getParameterType();

        if (MultiValueMap.class.isAssignableFrom(paramType)) {
            MultiValueMap<String, String> result;
            if (HttpHeaders.class.isAssignableFrom(paramType)) {
                result = new HttpHeaders();
            }
            else {
                result = new LinkedMultiValueMap<String, String>();
            }
            for (Iterator<String> iterator = webRequest.getHeaderNames(); iterator.hasNext();) {
                String headerName = iterator.next();
                for (String headerValue : webRequest.getHeaderValues(headerName)) {
                    result.add(headerName, headerValue);
                }
            }
            return result;
        }
        else {
            Map<String, String> result = new LinkedHashMap<String, String>();
            for (Iterator<String> iterator = webRequest.getHeaderNames(); iterator.hasNext();) {
                String headerName = iterator.next();
                String headerValue = webRequest.getHeader(headerName);
                result.put(headerName, headerValue);
            }
            return result;
        }
    }
}
