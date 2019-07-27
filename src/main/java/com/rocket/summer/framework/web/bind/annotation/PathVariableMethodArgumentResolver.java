package com.rocket.summer.framework.web.bind.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.ServletRequestBindingException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.HandlerMapping;
import com.rocket.summer.framework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves method arguments annotated with an @{@link PathVariable}.
 *
 * <p>An @{@link PathVariable} is a named value that gets resolved from a URI template variable. It is always
 * required and does not have a default value to fall back on. See the base class
 * {@link com.rocket.summer.framework.web.method.annotation.AbstractNamedValueMethodArgumentResolver} for more information on how named values are processed.
 *
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved path variable values that
 * don't yet match the method parameter type.
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @since 3.1
 */
public class PathVariableMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    public PathVariableMethodArgumentResolver() {
        super(null);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathVariable.class);
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        PathVariable annotation = parameter.getParameterAnnotation(PathVariable.class);
        return new PathVariableNamedValueInfo(annotation);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Map<String, String> uriTemplateVars =
                (Map<String, String>) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return (uriTemplateVars != null) ? uriTemplateVars.get(name) : null;
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter param) throws ServletRequestBindingException {
        String paramType = param.getParameterType().getName();
        throw new ServletRequestBindingException(
                "Missing URI template variable '" + name + "' for method parameter type [" + paramType + "]");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleResolvedValue(Object arg,
                                       String name,
                                       MethodParameter parameter,
                                       ModelAndViewContainer mavContainer,
                                       NativeWebRequest request) {
        String key = View.PATH_VARIABLES;
        int scope = RequestAttributes.SCOPE_REQUEST;
        Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(key, scope);
        if (pathVars == null) {
            pathVars = new HashMap<String, Object>();
            request.setAttribute(key, pathVars, scope);
        }
        pathVars.put(name, arg);
    }

    private static class PathVariableNamedValueInfo extends NamedValueInfo {

        private PathVariableNamedValueInfo(PathVariable annotation) {
            super(annotation.value(), true, ValueConstants.DEFAULT_NONE);
        }
    }
}
