package com.rocket.summer.framework.web.servlet.mvc.method.annotation;


import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.validation.DataBinder;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.mvc.support.RedirectAttributes;
import com.rocket.summer.framework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.rocket.summer.framework.web.ui.ModelMap;

import java.util.Map;

/**
 * Resolves method arguments of type {@link RedirectAttributes}.
 *
 * <p>This resolver must be listed ahead of {@link com.rocket.summer.framework.web.method.annotation.ModelMethodProcessor} and
 * {@link com.rocket.summer.framework.web.method.annotation.MapMethodProcessor}, which support {@link Map} and {@link Model}
 * arguments both of which are "super" types of {@code RedirectAttributes}
 * and would also attempt to resolve a {@code RedirectAttributes} argument.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class RedirectAttributesMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        return RedirectAttributes.class.isAssignableFrom(parameter.getParameterType());
    }

    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        DataBinder dataBinder = binderFactory.createBinder(webRequest, null, null);
        ModelMap redirectAttributes  = new RedirectAttributesModelMap(dataBinder);
        mavContainer.setRedirectModel(redirectAttributes);
        return redirectAttributes;
    }

}
