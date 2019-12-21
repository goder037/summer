package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.support.SessionStatus;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

/**
 * Resolves a {@link SessionStatus} argument by obtaining it from
 * the {@link ModelAndViewContainer}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class SessionStatusMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        return SessionStatus.class.equals(parameter.getParameterType());
    }

    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        return mavContainer.getSessionStatus();
    }

}
