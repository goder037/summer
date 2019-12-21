package com.rocket.summer.framework.web.method.support;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves method parameters by delegating to a list of registered {@link HandlerMethodArgumentResolver}s.
 * Previously resolved method parameters are cached for faster lookups.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {

    protected final Log logger = LogFactory.getLog(getClass());

    private final List<HandlerMethodArgumentResolver> argumentResolvers =
            new ArrayList<HandlerMethodArgumentResolver>();

    private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache =
            new ConcurrentHashMap<MethodParameter, HandlerMethodArgumentResolver>();

    /**
     * Return a read-only list with the contained resolvers, or an empty list.
     */
    public List<HandlerMethodArgumentResolver> getResolvers() {
        return Collections.unmodifiableList(this.argumentResolvers);
    }

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is supported by any registered
     * {@link HandlerMethodArgumentResolver}.
     */
    public boolean supportsParameter(MethodParameter parameter) {
        return getArgumentResolver(parameter) != null;
    }

    /**
     * Iterate over registered {@link HandlerMethodArgumentResolver}s and invoke the one that supports it.
     * @exception IllegalStateException if no suitable {@link HandlerMethodArgumentResolver} is found.
     */
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
        Assert.notNull(resolver, "Unknown parameter type [" + parameter.getParameterType().getName() + "]");
        return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    /**
     * Find a registered {@link HandlerMethodArgumentResolver} that supports the given method parameter.
     */
    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            for (HandlerMethodArgumentResolver methodArgumentResolver : argumentResolvers) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Testing if argument resolver [" + methodArgumentResolver + "] supports [" +
                            parameter.getGenericParameterType() + "]");
                }
                if (methodArgumentResolver.supportsParameter(parameter)) {
                    result = methodArgumentResolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Add the given {@link HandlerMethodArgumentResolver}.
     */
    public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver argumentResolver) {
        this.argumentResolvers.add(argumentResolver);
        return this;
    }

    /**
     * Add the given {@link HandlerMethodArgumentResolver}s.
     */
    public HandlerMethodArgumentResolverComposite addResolvers(
            List<? extends HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers != null) {
            for (HandlerMethodArgumentResolver resolver : argumentResolvers) {
                this.argumentResolvers.add(resolver);
            }
        }
        return this;
    }

}

