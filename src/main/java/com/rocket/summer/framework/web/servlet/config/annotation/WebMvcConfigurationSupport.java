package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.BeanInitializationException;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.format.Formatter;
import com.rocket.summer.framework.format.FormatterRegistry;
import com.rocket.summer.framework.format.support.DefaultFormattingConversionService;
import com.rocket.summer.framework.format.support.FormattingConversionService;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.ByteArrayHttpMessageConverter;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.converter.ResourceHttpMessageConverter;
import com.rocket.summer.framework.http.converter.StringHttpMessageConverter;
import com.rocket.summer.framework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.SourceHttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.validation.Errors;
import com.rocket.summer.framework.validation.MessageCodesResolver;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.web.HttpRequestHandler;
import com.rocket.summer.framework.web.accept.ContentNegotiationManager;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.ExceptionHandler;
import com.rocket.summer.framework.web.bind.annotation.ResponseStatus;
import com.rocket.summer.framework.web.bind.support.ConfigurableWebBindingInitializer;
import com.rocket.summer.framework.web.context.ServletContextAware;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.servlet.HandlerAdapter;
import com.rocket.summer.framework.web.servlet.HandlerExceptionResolver;
import com.rocket.summer.framework.web.servlet.HandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.BeanNameUrlHandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.ConversionServiceExposingInterceptor;
import com.rocket.summer.framework.web.servlet.handler.HandlerExceptionResolverComposite;
import com.rocket.summer.framework.web.servlet.mvc.Controller;
import com.rocket.summer.framework.web.servlet.mvc.HttpRequestHandlerAdapter;
import com.rocket.summer.framework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import com.rocket.summer.framework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.rocket.summer.framework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the main class providing the configuration behind the MVC Java config.
 * It is typically imported by adding {@link EnableWebMvc @EnableWebMvc} to an
 * application {@link Configuration @Configuration} class. An alternative more
 * advanced option is to extend directly from this class and override methods as
 * necessary remembering to add {@link Configuration @Configuration} to the
 * subclass and {@link Bean @Bean} to overridden {@link Bean @Bean} methods.
 * For more details see the Javadoc of {@link EnableWebMvc @EnableWebMvc}.
 *
 * <p>This class registers the following {@link HandlerMapping}s:</p>
 * <ul>
 * 	<li>{@link RequestMappingHandlerMapping}
 * 	ordered at 0 for mapping requests to annotated controller methods.
 * 	<li>{@link HandlerMapping}
 * 	ordered at 1 to map URL paths directly to view names.
 * 	<li>{@link BeanNameUrlHandlerMapping}
 * 	ordered at 2 to map URL paths to controller bean names.
 * 	<li>{@link HandlerMapping}
 * 	ordered at {@code Integer.MAX_VALUE-1} to serve static resource requests.
 * 	<li>{@link HandlerMapping}
 * 	ordered at {@code Integer.MAX_VALUE} to forward requests to the default servlet.
 * </ul>
 *
 * <p>Registers these {@link HandlerAdapter}s:
 * <ul>
 * 	<li>{@link RequestMappingHandlerAdapter}
 * 	for processing requests with annotated controller methods.
 * 	<li>{@link HttpRequestHandlerAdapter}
 * 	for processing requests with {@link HttpRequestHandler}s.
 * 	<li>{@link SimpleControllerHandlerAdapter}
 * 	for processing requests with interface-based {@link Controller}s.
 * </ul>
 *
 * <p>Registers a {@link HandlerExceptionResolverComposite} with this chain of
 * exception resolvers:
 * <ul>
 * 	<li>{@link ExceptionHandlerExceptionResolver} for handling exceptions
 * 	through @{@link ExceptionHandler} methods.
 * 	<li>{@link ResponseStatusExceptionResolver} for exceptions annotated
 * 	with @{@link ResponseStatus}.
 * 	<li>{@link DefaultHandlerExceptionResolver} for resolving known Spring
 * 	exception types
 * </ul>
 *
 * <p>Both the {@link RequestMappingHandlerAdapter} and the
 * {@link ExceptionHandlerExceptionResolver} are configured with default
 * instances of the following kind, unless custom instances are provided:
 * <ul>
 * 	<li>A {@link DefaultFormattingConversionService}
 * 	<li>A {@link LocalValidatorFactoryBean} if a JSR-303 implementation is
 * 	available on the classpath
 * 	<li>A range of {@link HttpMessageConverter}s depending on the 3rd party
 * 	libraries available on the classpath.
 * </ul>
 *
 * @see EnableWebMvc
 * @see WebMvcConfigurer
 * @see WebMvcConfigurerAdapter
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class WebMvcConfigurationSupport implements ApplicationContextAware, ServletContextAware {

    private static final boolean jaxb2Present =
            ClassUtils.isPresent("javax.xml.bind.Binder",
                    WebMvcConfigurationSupport.class.getClassLoader());

    private static final boolean jackson2Present =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
                    WebMvcConfigurationSupport.class.getClassLoader()) &&
                    ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
                            WebMvcConfigurationSupport.class.getClassLoader());

    private static final boolean jackson2XmlPresent =
            ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper",
                    WebMvcConfigurationSupport.class.getClassLoader());

    private ServletContext servletContext;

    private ApplicationContext applicationContext;

    private List<Object> interceptors;

    private ContentNegotiationManager contentNegotiationManager;

    private List<HttpMessageConverter<?>> messageConverters;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Return a {@link RequestMappingHandlerMapping} ordered at 0 for mapping
     * requests to annotated controllers.
     */
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        return handlerMapping;
    }

    /**
     * Protected method for plugging in a custom subclass of
     * {@link RequestMappingHandlerAdapter}.
     * @since 4.3
     */
    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }

    /**
     * Return a {@link ContentNegotiationManager} instance to use to determine
     * requested {@linkplain MediaType media types} in a given request.
     */
    @Bean
    public ContentNegotiationManager mvcContentNegotiationManager() {
        if (this.contentNegotiationManager == null) {
            ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(this.servletContext);
            configurer.mediaTypes(getDefaultMediaTypes());
            configureContentNegotiation(configurer);
            this.contentNegotiationManager = configurer.buildContentNegotiationManager();
        }
        return this.contentNegotiationManager;
    }

    protected Map<String, MediaType> getDefaultMediaTypes() {
        Map<String, MediaType> map = new HashMap<String, MediaType>(4);
        if (jaxb2Present || jackson2XmlPresent) {
            map.put("xml", MediaType.APPLICATION_XML);
        }
        if (jackson2Present) {
            map.put("json", MediaType.APPLICATION_JSON);
        }
        return map;
    }

    /**
     * Return the associated Spring {@link ApplicationContext}.
     * @since 4.2
     */
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    /**
     * Override this method to configure content negotiation.
     * @see DefaultServletHandlerConfigurer
     */
    protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    }

    /**
     * Return the {@link ConfigurableWebBindingInitializer} to use for
     * initializing all {@link WebDataBinder} instances.
     */
    protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer() {
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        initializer.setConversionService(mvcConversionService());
        initializer.setValidator(mvcValidator());
        initializer.setMessageCodesResolver(getMessageCodesResolver());
        return initializer;
    }

    /**
     * Protected method for plugging in a custom subclass of
     * {@link ExceptionHandlerExceptionResolver}.
     * @since 4.3
     */
    protected ExceptionHandlerExceptionResolver createExceptionHandlerExceptionResolver() {
        return new ExceptionHandlerExceptionResolver();
    }

    /**
     * Override this method to provide a custom {@link MessageCodesResolver}.
     */
    protected MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    /**
     * Protected method for plugging in a custom subclass of
     * {@link RequestMappingHandlerMapping}.
     * @since 4.0
     */
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    /**
     * Provide access to the shared handler interceptors used to configure
     * {@link HandlerMapping} instances with. This method cannot be overridden,
     * use {@link #addInterceptors(InterceptorRegistry)} instead.
     */
    protected final Object[] getInterceptors() {
        if (interceptors == null) {
            InterceptorRegistry registry = new InterceptorRegistry();
            addInterceptors(registry);
            registry.addInterceptor(new ConversionServiceExposingInterceptor(mvcConversionService()));
            interceptors = registry.getInterceptors();
        }
        return interceptors.toArray();
    }

    /**
     * Override this method to add Spring MVC interceptors for
     * pre- and post-processing of controller invocation.
     * @see InterceptorRegistry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
    }

    /**
     * Return a handler mapping ordered at 1 to map URL paths directly to
     * view names. To configure view controllers, override
     * {@link #addViewControllers}.
     */
    @Bean
    public HandlerMapping viewControllerHandlerMapping() {
        ViewControllerRegistry registry = new ViewControllerRegistry();
        addViewControllers(registry);

        AbstractHandlerMapping handlerMapping = registry.getHandlerMapping();
        handlerMapping = handlerMapping != null ? handlerMapping : new EmptyHandlerMapping();
        handlerMapping.setInterceptors(getInterceptors());
        return handlerMapping;
    }

    /**
     * Override this method to add view controllers.
     * @see ViewControllerRegistry
     */
    protected void addViewControllers(ViewControllerRegistry registry) {
    }

    /**
     * Return a {@link BeanNameUrlHandlerMapping} ordered at 2 to map URL
     * paths to controller bean names.
     */
    @Bean
    public BeanNameUrlHandlerMapping beanNameHandlerMapping() {
        BeanNameUrlHandlerMapping mapping = new BeanNameUrlHandlerMapping();
        mapping.setOrder(2);
        mapping.setInterceptors(getInterceptors());
        return mapping;
    }

    /**
     * Return a handler mapping ordered at Integer.MAX_VALUE-1 with mapped
     * resource handlers. To configure resource handling, override
     * {@link #addResourceHandlers}.
     */
    @Bean
    public HandlerMapping resourceHandlerMapping() {
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(applicationContext, servletContext);
        addResourceHandlers(registry);
        AbstractHandlerMapping handlerMapping = registry.getHandlerMapping();
        handlerMapping = handlerMapping != null ? handlerMapping : new EmptyHandlerMapping();
        return handlerMapping;
    }

    /**
     * Override this method to add resource handlers for serving static resources.
     * @see ResourceHandlerRegistry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    /**
     * Return a handler mapping ordered at Integer.MAX_VALUE with a mapped
     * default servlet handler. To configure "default" Servlet handling,
     * override {@link #configureDefaultServletHandling}.
     */
    @Bean
    public HandlerMapping defaultServletHandlerMapping() {
        DefaultServletHandlerConfigurer configurer = new DefaultServletHandlerConfigurer(servletContext);
        configureDefaultServletHandling(configurer);
        AbstractHandlerMapping handlerMapping = configurer.getHandlerMapping();
        handlerMapping = handlerMapping != null ? handlerMapping : new EmptyHandlerMapping();
        return handlerMapping;
    }

    /**
     * Override this method to configure "default" Servlet handling.
     * @see DefaultServletHandlerConfigurer
     */
    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }

    /**
     * Returns a {@link RequestMappingHandlerAdapter} for processing requests
     * through annotated controller methods. Consider overriding one of these
     * other more fine-grained methods:
     * <ul>
     *  <li>{@link #addArgumentResolvers} for adding custom argument resolvers.
     * 	<li>{@link #addReturnValueHandlers} for adding custom return value handlers.
     * 	<li>{@link #configureMessageConverters} for adding custom message converters.
     * </ul>
     */
    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
        webBindingInitializer.setConversionService(mvcConversionService());
        webBindingInitializer.setValidator(mvcValidator());

        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
        addArgumentResolvers(argumentResolvers);

        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
        addReturnValueHandlers(returnValueHandlers);

        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
        adapter.setMessageConverters(getMessageConverters());
        adapter.setWebBindingInitializer(webBindingInitializer);
        adapter.setCustomArgumentResolvers(argumentResolvers);
        adapter.setCustomReturnValueHandlers(returnValueHandlers);
        return adapter;
    }

    /**
     * Add custom {@link HandlerMethodArgumentResolver}s to use in addition to
     * the ones registered by default.
     * <p>Custom argument resolvers are invoked before built-in resolvers
     * except for those that rely on the presence of annotations (e.g.
     * {@code @RequestParameter}, {@code @PathVariable}, etc.).
     * The latter can  be customized by configuring the
     * {@link RequestMappingHandlerAdapter} directly.
     * @param argumentResolvers the list of custom converters;
     * 	initially an empty list.
     */
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    /**
     * Add custom {@link HandlerMethodReturnValueHandler}s in addition to the
     * ones registered by default.
     * <p>Custom return value handlers are invoked before built-in ones except
     * for those that rely on the presence of annotations (e.g.
     * {@code @ResponseBody}, {@code @ModelAttribute}, etc.).
     * The latter can be customized by configuring the
     * {@link RequestMappingHandlerAdapter} directly.
     * @param returnValueHandlers the list of custom handlers;
     * initially an empty list.
     */
    protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    }

    /**
     * Provides access to the shared {@link HttpMessageConverter}s used by the
     * {@link RequestMappingHandlerAdapter} and the
     * {@link ExceptionHandlerExceptionResolver}.
     * This method cannot be overridden.
     * Use {@link #configureMessageConverters(List)} instead.
     * Also see {@link #addDefaultHttpMessageConverters(List)} that can be
     * used to add default message converters.
     */
    protected final List<HttpMessageConverter<?>> getMessageConverters() {
        if (messageConverters == null) {
            messageConverters = new ArrayList<HttpMessageConverter<?>>();
            configureMessageConverters(messageConverters);
            if (messageConverters.isEmpty()) {
                addDefaultHttpMessageConverters(messageConverters);
            }
        }
        return messageConverters;
    }

    /**
     * Override this method to add custom {@link HttpMessageConverter}s to use
     * with the {@link RequestMappingHandlerAdapter} and the
     * {@link ExceptionHandlerExceptionResolver}. Adding converters to the
     * list turns off the default converters that would otherwise be registered
     * by default. Also see {@link #addDefaultHttpMessageConverters(List)} that
     * can be used to add default message converters.
     * @param converters a list to add message converters to;
     * initially an empty list.
     */
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    /**
     * Adds a set of default HttpMessageConverter instances to the given list.
     * Subclasses can call this method from {@link #configureMessageConverters(List)}.
     * @param messageConverters the list to add the default message converters to
     */
    protected final void addDefaultHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);

        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(stringConverter);
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter<Source>());
        messageConverters.add(new XmlAwareFormHttpMessageConverter());

        ClassLoader classLoader = getClass().getClassLoader();
        if (ClassUtils.isPresent("javax.xml.bind.Binder", classLoader)) {
            messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader)) {
            messageConverters.add(new MappingJackson2HttpMessageConverter());
        }
        /*else if (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", classLoader)) {
            messageConverters.add(new MappingJackson2HttpMessageConverter());
        }*/
//        if (ClassUtils.isPresent("com.sun.syndication.feed.WireFeed", classLoader)) {
//            messageConverters.add(new AtomFeedHttpMessageConverter());
//            messageConverters.add(new RssChannelHttpMessageConverter());
//        }
    }

    /**
     * Returns a {@link FormattingConversionService} for use with annotated
     * controller methods and the {@code spring:eval} JSP tag.
     * Also see {@link #addFormatters} as an alternative to overriding this method.
     */
    @Bean
    public FormattingConversionService mvcConversionService() {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        addFormatters(conversionService);
        return conversionService;
    }

    /**
     * Override this method to add custom {@link Converter}s and {@link Formatter}s.
     */
    protected void addFormatters(FormatterRegistry registry) {
    }

    /**
     * Returns a global {@link Validator} instance for example for validating
     * {@code @ModelAttribute} and {@code @RequestBody} method arguments.
     * Delegates to {@link #getValidator()} first and if that returns {@code null}
     * checks the classpath for the presence of a JSR-303 implementations
     * before creating a {@code LocalValidatorFactoryBean}.If a JSR-303
     * implementation is not available, a no-op {@link Validator} is returned.
     */
    @Bean
    public Validator mvcValidator() {
        Validator validator = getValidator();
        if (validator == null) {
            if (ClassUtils.isPresent("javax.validation.Validator", getClass().getClassLoader())) {
                Class<?> clazz;
                try {
                    String className = "com.rocket.summer.framework.validation.beanvalidation.LocalValidatorFactoryBean";
                    clazz = ClassUtils.forName(className, WebMvcConfigurationSupport.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new BeanInitializationException("Could not find default validator", e);
                } catch (LinkageError e) {
                    throw new BeanInitializationException("Could not find default validator", e);
                }
                validator = (Validator) BeanUtils.instantiate(clazz);
            }
            else {
                validator = new Validator() {
                    public boolean supports(Class<?> clazz) {
                        return false;
                    }
                    public void validate(Object target, Errors errors) {
                    }
                };
            }
        }
        return validator;
    }

    /**
     * Override this method to provide a custom {@link Validator}.
     */
    protected Validator getValidator() {
        return null;
    }

    /**
     * Returns a {@link HttpRequestHandlerAdapter} for processing requests
     * with {@link HttpRequestHandler}s.
     */
    @Bean
    public HttpRequestHandlerAdapter httpRequestHandlerAdapter() {
        return new HttpRequestHandlerAdapter();
    }

    /**
     * Returns a {@link SimpleControllerHandlerAdapter} for processing requests
     * with interface-based controllers.
     */
    @Bean
    public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
        return new SimpleControllerHandlerAdapter();
    }

    /**
     * Returns a {@link HandlerExceptionResolverComposite} containing a list
     * of exception resolvers obtained either through
     * {@link #configureHandlerExceptionResolvers(List)} or through
     * {@link #addDefaultHandlerExceptionResolvers(List)}.
     * <p><strong>Note:</strong> This method cannot be made final due to CGLib
     * constraints. Rather than overriding it, consider overriding
     * {@link #configureHandlerExceptionResolvers(List)}, which allows
     * providing a list of resolvers.
     */
    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        List<HandlerExceptionResolver> exceptionResolvers = new ArrayList<HandlerExceptionResolver>();
        configureHandlerExceptionResolvers(exceptionResolvers);

        if (exceptionResolvers.isEmpty()) {
            addDefaultHandlerExceptionResolvers(exceptionResolvers);
        }

        HandlerExceptionResolverComposite composite = new HandlerExceptionResolverComposite();
        composite.setOrder(0);
        composite.setExceptionResolvers(exceptionResolvers);
        return composite;
    }

    /**
     * Override this method to configure the list of
     * {@link HandlerExceptionResolver}s to use. Adding resolvers to the list
     * turns off the default resolvers that would otherwise be registered by
     * default. Also see {@link #addDefaultHandlerExceptionResolvers(List)}
     * that can be used to add the default exception resolvers.
     * @param exceptionResolvers a list to add exception resolvers to;
     * initially an empty list.
     */
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    /**
     * A method available to subclasses for adding default
     * {@link HandlerExceptionResolver}s.
     * <p>Adds the following exception resolvers:
     * <ul>
     * 	<li>{@link ExceptionHandlerExceptionResolver}
     * 	for handling exceptions through @{@link ExceptionHandler} methods.
     * 	<li>{@link ResponseStatusExceptionResolver}
     * 	for exceptions annotated with @{@link ResponseStatus}.
     * 	<li>{@link DefaultHandlerExceptionResolver}
     * 	for resolving known Spring exception types
     * </ul>
     */
    protected final void addDefaultHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setMessageConverters(getMessageConverters());
        exceptionHandlerExceptionResolver.afterPropertiesSet();

        exceptionResolvers.add(exceptionHandlerExceptionResolver);
        exceptionResolvers.add(new ResponseStatusExceptionResolver());
        exceptionResolvers.add(new DefaultHandlerExceptionResolver());
    }

    private final static class EmptyHandlerMapping extends AbstractHandlerMapping {

        @Override
        protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
            return null;
        }
    }

}
