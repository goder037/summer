package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.support.ServletUriComponentsBuilder;
import com.rocket.summer.framework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Resolvers argument values of type {@link UriComponentsBuilder}.
 *
 * <p>The returned instance is initialized via
 * {@link ServletUriComponentsBuilder#fromServletMapping(HttpServletRequest)}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class UriComponentsBuilderMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        return UriComponentsBuilder.class.isAssignableFrom(parameter.getParameterType());
    }

    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return ServletUriComponentsBuilder.fromServletMapping(request);
    }

}
