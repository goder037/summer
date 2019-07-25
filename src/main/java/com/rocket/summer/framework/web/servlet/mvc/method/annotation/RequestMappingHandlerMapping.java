package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.stereotype.Controller;
import com.rocket.summer.framework.web.bind.annotation.RequestMapping;
import com.rocket.summer.framework.web.servlet.mvc.condition.*;
import com.rocket.summer.framework.web.servlet.mvc.method.RequestMappingInfo;
import com.rocket.summer.framework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.lang.reflect.Method;

/**
 * Creates {@link RequestMappingInfo} instances from type and method-level
 * {@link RequestMapping @RequestMapping} annotations in
 * {@link Controller @Controller} classes.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class RequestMappingHandlerMapping extends RequestMappingInfoHandlerMapping {

    private boolean useSuffixPatternMatch = true;

    private boolean useTrailingSlashMatch = true;

    /**
     * Whether to use suffix pattern match (".*") when matching patterns to
     * requests. If enabled a method mapped to "/users" also matches to "/users.*".
     * <p>The default value is {@code true}.
     */
    public void setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
        this.useSuffixPatternMatch = useSuffixPatternMatch;
    }

    /**
     * Whether to match to URLs irrespective of the presence of a trailing slash.
     * If enabled a method mapped to "/users" also matches to "/users/".
     * <p>The default value is {@code true}.
     */
    public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
        this.useTrailingSlashMatch = useTrailingSlashMatch;
    }

    /**
     * Whether to use suffix pattern matching.
     */
    public boolean useSuffixPatternMatch() {
        return this.useSuffixPatternMatch;
    }
    /**
     * Whether to match to URLs irrespective of the presence of a trailing  slash.
     */
    public boolean useTrailingSlashMatch() {
        return this.useTrailingSlashMatch;
    }

    /**
     * {@inheritDoc}
     * Expects a handler to have a type-level @{@link Controller} annotation.
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return ((AnnotationUtils.findAnnotation(beanType, Controller.class) != null) ||
                (AnnotationUtils.findAnnotation(beanType, RequestMapping.class) != null));
    }

    /**
     * Uses method and type-level @{@link RequestMapping} annotations to create
     * the RequestMappingInfo.
     *
     * @return the created RequestMappingInfo, or {@code null} if the method
     * does not have a {@code @RequestMapping} annotation.
     *
     * @see #getCustomMethodCondition(Method)
     * @see #getCustomTypeCondition(Class)
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = null;
        RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);
            info = createRequestMappingInfo(methodAnnotation, methodCondition);
            RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                info = createRequestMappingInfo(typeAnnotation, typeCondition).combine(info);
            }
        }
        return info;
    }

    /**
     * Provide a custom method-level request condition.
     * The custom {@link RequestCondition} can be of any type so long as the
     * same condition type is returned from all calls to this method in order
     * to ensure custom request conditions can be combined and compared.
     * @param method the handler method for which to create the condition
     * @return the condition, or {@code null}
     */
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return null;
    }

    /**
     * Provide a custom type-level request condition.
     * The custom {@link RequestCondition} can be of any type so long as the
     * same condition type is returned from all calls to this method in order
     * to ensure custom request conditions can be combined and compared.
     * @param handlerType the handler type for which to create the condition
     * @return the condition, or {@code null}
     */
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return null;
    }

    /**
     * Created a RequestMappingInfo from a RequestMapping annotation.
     */
    private RequestMappingInfo createRequestMappingInfo(RequestMapping annotation, RequestCondition<?> customCondition) {
        return new RequestMappingInfo(
                new PatternsRequestCondition(annotation.value(),
                        getUrlPathHelper(), getPathMatcher(), this.useSuffixPatternMatch, this.useTrailingSlashMatch),
                new RequestMethodsRequestCondition(annotation.method()),
                new ParamsRequestCondition(annotation.params()),
                new HeadersRequestCondition(annotation.headers()),
                new ConsumesRequestCondition(annotation.consumes(), annotation.headers()),
                new ProducesRequestCondition(annotation.produces(), annotation.headers()),
                customCondition);
    }

}
