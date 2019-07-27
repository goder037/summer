package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.ServletRequestBindingException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.RequestHeader;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;

import java.util.Map;

/**
 * Resolves method arguments annotated with {@code @RequestHeader} except for
 * {@link Map} arguments. See {@link RequestHeaderMapMethodArgumentResolver} for
 * details on {@link Map} arguments annotated with {@code @RequestHeader}.
 *
 * <p>An {@code @RequestHeader} is a named value resolved from a request header.
 * It has a required flag and a default value to fall back on when the request
 * header does not exist.
 *
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved
 * request header values that don't yet match the method parameter type.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class RequestHeaderMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    /**
     * @param beanFactory a bean factory to use for resolving  ${...}
     * placeholder and #{...} SpEL expressions in default values;
     * or {@code null} if default values are not expected to have expressions
     */
    public RequestHeaderMethodArgumentResolver(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class)
                && !Map.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestHeader annotation = parameter.getParameterAnnotation(RequestHeader.class);
        return new RequestHeaderNamedValueInfo(annotation);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        String[] headerValues = request.getHeaderValues(name);
        if (headerValues != null) {
            return (headerValues.length == 1 ? headerValues[0] : headerValues);
        }
        else {
            return null;
        }
    }

    @Override
    protected void handleMissingValue(String headerName, MethodParameter param) throws ServletRequestBindingException {
        String paramType = param.getParameterType().getName();
        throw new ServletRequestBindingException(
                "Missing header '" + headerName + "' for method parameter type [" + paramType + "]");
    }

    private static class RequestHeaderNamedValueInfo extends NamedValueInfo {

        private RequestHeaderNamedValueInfo(RequestHeader annotation) {
            super(annotation.value(), annotation.required(), annotation.defaultValue());
        }
    }
}
