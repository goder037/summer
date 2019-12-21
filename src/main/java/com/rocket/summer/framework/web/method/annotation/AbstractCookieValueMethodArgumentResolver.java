package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.ServletRequestBindingException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.CookieValue;

/**
 * A base abstract class to resolve method arguments annotated with
 * {@code @CookieValue}. Subclasses extract the cookie value from the request.
 *
 * <p>An {@code @CookieValue} is a named value that is resolved from a cookie.
 * It has a required flag and a default value to fall back on when the cookie
 * does not exist.
 *
 * <p>A {@link WebDataBinder} may be invoked to apply type conversion to the
 * resolved cookie value.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractCookieValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    /**
     * @param beanFactory a bean factory to use for resolving  ${...}
     * placeholder and #{...} SpEL expressions in default values;
     * or {@code null} if default values are not expected to contain expressions
     */
    public AbstractCookieValueMethodArgumentResolver(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CookieValue.class);
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        CookieValue annotation = parameter.getParameterAnnotation(CookieValue.class);
        return new CookieValueNamedValueInfo(annotation);
    }

    @Override
    protected void handleMissingValue(String cookieName, MethodParameter param) throws ServletRequestBindingException {
        String paramType = param.getParameterType().getName();
        throw new ServletRequestBindingException(
                "Missing cookie named '" + cookieName + "' for method parameter type [" + paramType + "]");
    }

    private static class CookieValueNamedValueInfo extends NamedValueInfo {

        private CookieValueNamedValueInfo(CookieValue annotation) {
            super(annotation.value(), annotation.required(), annotation.defaultValue());
        }
    }
}
