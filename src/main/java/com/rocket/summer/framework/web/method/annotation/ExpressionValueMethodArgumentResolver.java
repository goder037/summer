package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.beans.factory.annotation.Value;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;

import javax.servlet.ServletException;

/**
 * Resolves method arguments annotated with {@code @Value}.
 *
 * <p>An {@code @Value} does not have a name but gets resolved from the default
 * value string, which may contain ${...} placeholder or Spring Expression
 * Language #{...} expressions.
 *
 * <p>A {@link WebDataBinder} may be invoked to apply type conversion to
 * resolved argument value.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ExpressionValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    /**
     * @param beanFactory a bean factory to use for resolving  ${...}
     * placeholder and #{...} SpEL expressions in default values;
     * or {@code null} if default values are not expected to contain expressions
     */
    public ExpressionValueMethodArgumentResolver(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Value.class);
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        Value annotation = parameter.getParameterAnnotation(Value.class);
        return new ExpressionValueNamedValueInfo(annotation);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest webRequest)
            throws Exception {
        // No name to resolve
        return null;
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new UnsupportedOperationException("@Value is never required: " + parameter.getMethod());
    }

    private static class ExpressionValueNamedValueInfo extends NamedValueInfo {

        private ExpressionValueNamedValueInfo(Value annotation) {
            super("@Value", false, annotation.value());
        }
    }
}
