package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.format.Formatter;
import com.rocket.summer.framework.format.FormatterRegistry;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.servlet.HandlerExceptionResolver;

import java.util.List;

/**
 * Defines callback methods to customize the Java-based configuration for
 * Spring MVC enabled via {@code @EnableWebMvc}.
 *
 * <p>{@code @EnableWebMvc}-annotated configuration classes may implement
 * this interface to be called back and given a chance to customize the
 * default configuration. Consider extending {@link WebMvcConfigurerAdapter},
 * which provides a stub implementation of all interface methods.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @author David Syer
 * @since 3.1
 */
public interface WebMvcConfigurer {

    /**
     * Add {@link Converter}s and {@link Formatter}s in addition to the ones
     * registered by default.
     */
    void addFormatters(FormatterRegistry registry);

    /**
     * Configure the {@link HttpMessageConverter}s to use in argument resolvers
     * and return value handlers that support reading and/or writing to the
     * body of the request and response. If no message converters are added to
     * the list, default converters are added instead.
     * @param converters initially an empty list of converters
     */
    void configureMessageConverters(List<HttpMessageConverter<?>> converters);

    /**
     * Provide a custom {@link Validator} instead of the one created by default.
     * The default implementation, assuming JSR-303 is on the classpath, is:
     * {@link com.rocket.summer.framework.validation.beanvalidation.LocalValidatorFactoryBean}.
     * Leave the return value as {@code null} to keep the default.
     */
    Validator getValidator();

    /**
     * Add resolvers to support custom controller method argument types.
     * <p>This does not override the built-in support for resolving handler
     * method arguments. To customize the built-in support for argument
     * resolution, configure {@link RequestMappingHandlerAdapter} directly.
     * @param argumentResolvers initially an empty list
     */
    void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers);

    /**
     * Add handlers to support custom controller method return value types.
     * <p>Using this option does not override the built-in support for handling
     * return values. To customize the built-in support for handling return
     * values, configure RequestMappingHandlerAdapter directly.
     * @param returnValueHandlers initially an empty list
     */
    void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers);

    /**
     * Configure the {@link HandlerExceptionResolver}s to handle unresolved
     * controller exceptions. If no resolvers are added to the list, default
     * exception resolvers are added instead.
     * @param exceptionResolvers initially an empty list
     */
    void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers);

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations. Interceptors can be registered to apply
     * to all requests or be limited to a subset of URL patterns.
     */
    void addInterceptors(InterceptorRegistry registry);

    /**
     * Add view controllers to create a direct mapping between a URL path and
     * view name without the need for a controller in between.
     */
    void addViewControllers(ViewControllerRegistry registry);

    /**
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     */
    void addResourceHandlers(ResourceHandlerRegistry registry);

    /**
     * Configure a handler to delegate unhandled requests by forwarding to the
     * Servlet container's "default" servlet. A common use case for this is when
     * the {@link DispatcherServlet} is mapped to "/" thus overriding the
     * Servlet container's default handling of static resources.
     */
    void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer);

}
