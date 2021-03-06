package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.validation.BindingResult;
import com.rocket.summer.framework.validation.Errors;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.ui.ModelMap;

import java.util.ArrayList;

/**
 * Resolves {@link Errors} method arguments.
 *
 * <p>An {@code Errors} method argument is expected to appear immediately after
 * the model attribute in the method signature. It is resolved by expecting the
 * last two attributes added to the model to be the model attribute and its
 * {@link BindingResult}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ErrorsMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return Errors.class.isAssignableFrom(paramType);
    }

    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        ModelMap model = mavContainer.getModel();
        if (model.size() > 0) {
            int lastIndex = model.size()-1;
            String lastKey = new ArrayList<String>(model.keySet()).get(lastIndex);
            if (lastKey.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
                return model.get(lastKey);
            }
        }

        throw new IllegalStateException(
                "An Errors/BindingResult argument is expected to be immediately after the model attribute " +
                        "argument in the controller method signature: " + parameter.getMethod());
    }

}