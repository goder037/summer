package com.rocket.summer.framework.boot.autoconfigure.web;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.ListableBeanFactory;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.beans.factory.ObjectProvider;
import com.rocket.summer.framework.beans.factory.annotation.Autowired;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureAfter;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureOrder;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import com.rocket.summer.framework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.web.ResourceProperties.Strategy;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.boot.web.filter.OrderedHiddenHttpMethodFilter;
import com.rocket.summer.framework.boot.web.filter.OrderedRequestContextFilter;
import com.rocket.summer.framework.web.filter.OrderedHttpPutFormContentFilter;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.context.annotation.Lazy;
import com.rocket.summer.framework.context.annotation.Primary;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.format.Formatter;
import com.rocket.summer.framework.format.FormatterRegistry;
import com.rocket.summer.framework.format.datetime.DateFormatter;
import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.validation.DefaultMessageCodesResolver;
import com.rocket.summer.framework.validation.MessageCodesResolver;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.accept.ContentNegotiationManager;
import com.rocket.summer.framework.web.accept.ContentNegotiationStrategy;
import com.rocket.summer.framework.web.accept.PathExtensionContentNegotiationStrategy;
import com.rocket.summer.framework.web.bind.support.ConfigurableWebBindingInitializer;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.context.request.RequestContextListener;
import com.rocket.summer.framework.web.filter.HiddenHttpMethodFilter;
import com.rocket.summer.framework.web.filter.HttpPutFormContentFilter;
import com.rocket.summer.framework.web.filter.RequestContextFilter;
import com.rocket.summer.framework.web.servlet.DispatcherServlet;
import com.rocket.summer.framework.web.servlet.HandlerExceptionResolver;
import com.rocket.summer.framework.web.servlet.LocaleResolver;
import com.rocket.summer.framework.web.servlet.View;
import com.rocket.summer.framework.web.servlet.ViewResolver;
import com.rocket.summer.framework.web.servlet.config.annotation.AsyncSupportConfigurer;
import com.rocket.summer.framework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import com.rocket.summer.framework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import com.rocket.summer.framework.web.servlet.config.annotation.EnableWebMvc;
import com.rocket.summer.framework.web.servlet.config.annotation.ResourceChainRegistration;
import com.rocket.summer.framework.web.servlet.config.annotation.ResourceHandlerRegistration;
import com.rocket.summer.framework.web.servlet.config.annotation.ResourceHandlerRegistry;
import com.rocket.summer.framework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import com.rocket.summer.framework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerExceptionResolver;
import com.rocket.summer.framework.web.servlet.handler.AbstractUrlHandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping;
import com.rocket.summer.framework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import com.rocket.summer.framework.web.servlet.i18n.FixedLocaleResolver;
import com.rocket.summer.framework.web.servlet.mvc.ParameterizableViewController;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.rocket.summer.framework.web.servlet.resource.AppCacheManifestTransformer;
import com.rocket.summer.framework.web.servlet.resource.GzipResourceResolver;
import com.rocket.summer.framework.web.servlet.resource.ResourceHttpRequestHandler;
import com.rocket.summer.framework.web.servlet.resource.ResourceResolver;
import com.rocket.summer.framework.web.servlet.resource.VersionResourceResolver;
import com.rocket.summer.framework.web.servlet.view.BeanNameViewResolver;
import com.rocket.summer.framework.web.servlet.view.ContentNegotiatingViewResolver;
import com.rocket.summer.framework.web.servlet.view.InternalResourceViewResolver;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link EnableWebMvc Web MVC}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Sébastien Deleuze
 * @author Eddú Meléndez
 * @author Stephane Nicoll
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class,
        WebMvcConfigurerAdapter.class })
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class,
        ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {

    public static final String DEFAULT_PREFIX = "";

    public static final String DEFAULT_SUFFIX = "";

    @Bean
    @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }

    @Bean
    @ConditionalOnMissingBean(HttpPutFormContentFilter.class)
    @ConditionalOnProperty(prefix = "spring.mvc.formcontent.putfilter", name = "enabled",
            matchIfMissing = true)
    public OrderedHttpPutFormContentFilter httpPutFormContentFilter() {
        return new OrderedHttpPutFormContentFilter();
    }

    // Defined as a nested config to ensure WebMvcConfigurerAdapter is not read when not
    // on the classpath
    @Configuration
    @Import(EnableWebMvcConfiguration.class)
    @EnableConfigurationProperties({ WebMvcProperties.class, ResourceProperties.class })
    public static class WebMvcAutoConfigurationAdapter extends WebMvcConfigurerAdapter {

        private static final Log logger = LogFactory
                .getLog(WebMvcConfigurerAdapter.class);

        private final ResourceProperties resourceProperties;

        private final WebMvcProperties mvcProperties;

        private final ListableBeanFactory beanFactory;

        private final HttpMessageConverters messageConverters;

        final ResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer;

        public WebMvcAutoConfigurationAdapter(ResourceProperties resourceProperties,
                                              WebMvcProperties mvcProperties, ListableBeanFactory beanFactory,
                                              @Lazy HttpMessageConverters messageConverters,
                                              ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizerProvider) {
            this.resourceProperties = resourceProperties;
            this.mvcProperties = mvcProperties;
            this.beanFactory = beanFactory;
            this.messageConverters = messageConverters;
            this.resourceHandlerRegistrationCustomizer = resourceHandlerRegistrationCustomizerProvider
                    .getIfAvailable();
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.addAll(this.messageConverters.getConverters());
        }

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            Long timeout = this.mvcProperties.getAsync().getRequestTimeout();
            if (timeout != null) {
                configurer.setDefaultTimeout(timeout);
            }
        }

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            Map<String, MediaType> mediaTypes = this.mvcProperties.getMediaTypes();
            for (Entry<String, MediaType> mediaType : mediaTypes.entrySet()) {
                configurer.mediaType(mediaType.getKey(), mediaType.getValue());
            }
        }

        @Bean
        @ConditionalOnMissingBean
        public InternalResourceViewResolver defaultViewResolver() {
            InternalResourceViewResolver resolver = new InternalResourceViewResolver();
            resolver.setPrefix(this.mvcProperties.getView().getPrefix());
            resolver.setSuffix(this.mvcProperties.getView().getSuffix());
            return resolver;
        }

        @Bean
        @ConditionalOnBean(View.class)
        @ConditionalOnMissingBean
        public BeanNameViewResolver beanNameViewResolver() {
            BeanNameViewResolver resolver = new BeanNameViewResolver();
            resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
            return resolver;
        }

        @Bean
        @ConditionalOnBean(ViewResolver.class)
        @ConditionalOnMissingBean(name = "viewResolver",
                value = ContentNegotiatingViewResolver.class)
        public ContentNegotiatingViewResolver viewResolver(BeanFactory beanFactory) {
            ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
            resolver.setContentNegotiationManager(
                    beanFactory.getBean(ContentNegotiationManager.class));
            // ContentNegotiatingViewResolver uses all the other view resolvers to locate
            // a view so it should have a high precedence
            resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return resolver;
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
        public LocaleResolver localeResolver() {
            if (this.mvcProperties
                    .getLocaleResolver() == WebMvcProperties.LocaleResolver.FIXED) {
                return new FixedLocaleResolver(this.mvcProperties.getLocale());
            }
            AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
            localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
            return localeResolver;
        }

        @Bean
        @ConditionalOnProperty(prefix = "spring.mvc", name = "date-format")
        public Formatter<Date> dateFormatter() {
            return new DateFormatter(this.mvcProperties.getDateFormat());
        }

        @Override
        public MessageCodesResolver getMessageCodesResolver() {
            if (this.mvcProperties.getMessageCodesResolverFormat() != null) {
                DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
                resolver.setMessageCodeFormatter(
                        this.mvcProperties.getMessageCodesResolverFormat());
                return resolver;
            }
            return null;
        }

        @Override
        public void addFormatters(FormatterRegistry registry) {
            for (Converter<?, ?> converter : getBeansOfType(Converter.class)) {
                registry.addConverter(converter);
            }
            for (GenericConverter converter : getBeansOfType(GenericConverter.class)) {
                registry.addConverter(converter);
            }
            for (Formatter<?> formatter : getBeansOfType(Formatter.class)) {
                registry.addFormatter(formatter);
            }
        }

        private <T> Collection<T> getBeansOfType(Class<T> type) {
            return this.beanFactory.getBeansOfType(type).values();
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            if (!this.resourceProperties.isAddMappings()) {
                logger.debug("Default resource handling disabled");
                return;
            }
            Integer cachePeriod = this.resourceProperties.getCachePeriod();
            if (!registry.hasMappingForPattern("/webjars/**")) {
                customizeResourceHandlerRegistration(registry
                        .addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/")
                        .setCachePeriod(cachePeriod));
            }
            String staticPathPattern = this.mvcProperties.getStaticPathPattern();
            if (!registry.hasMappingForPattern(staticPathPattern)) {
                customizeResourceHandlerRegistration(
                        registry.addResourceHandler(staticPathPattern)
                                .addResourceLocations(
                                        this.resourceProperties.getStaticLocations())
                                .setCachePeriod(cachePeriod));
            }
        }

        @Bean
        public WelcomePageHandlerMapping welcomePageHandlerMapping(
                ResourceProperties resourceProperties) {
            return new WelcomePageHandlerMapping(resourceProperties.getWelcomePage(),
                    this.mvcProperties.getStaticPathPattern());
        }

        private void customizeResourceHandlerRegistration(
                ResourceHandlerRegistration registration) {
            if (this.resourceHandlerRegistrationCustomizer != null) {
                this.resourceHandlerRegistrationCustomizer.customize(registration);
            }

        }

        @Bean
        @ConditionalOnMissingBean({ RequestContextListener.class,
                RequestContextFilter.class })
        public static RequestContextFilter requestContextFilter() {
            return new OrderedRequestContextFilter();
        }

        @Configuration
        @ConditionalOnProperty(value = "spring.mvc.favicon.enabled",
                matchIfMissing = true)
        public static class FaviconConfiguration {

            private final ResourceProperties resourceProperties;

            public FaviconConfiguration(ResourceProperties resourceProperties) {
                this.resourceProperties = resourceProperties;
            }

            @Bean
            public SimpleUrlHandlerMapping faviconHandlerMapping() {
                SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
                mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
                mapping.setUrlMap(Collections.singletonMap("**/favicon.ico",
                        faviconRequestHandler()));
                return mapping;
            }

            @Bean
            public ResourceHttpRequestHandler faviconRequestHandler() {
                ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
                requestHandler
                        .setLocations(this.resourceProperties.getFaviconLocations());
                return requestHandler;
            }

        }

    }

    /**
     * Configuration equivalent to {@code @EnableWebMvc}.
     */
    @Configuration
    public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration {

        private final WebMvcProperties mvcProperties;

        private final ListableBeanFactory beanFactory;

        private final WebMvcRegistrations mvcRegistrations;

        public EnableWebMvcConfiguration(
                ObjectProvider<WebMvcProperties> mvcPropertiesProvider,
                ObjectProvider<WebMvcRegistrations> mvcRegistrationsProvider,
                ListableBeanFactory beanFactory) {
            this.mvcProperties = mvcPropertiesProvider.getIfAvailable();
            this.mvcRegistrations = mvcRegistrationsProvider.getIfUnique();
            this.beanFactory = beanFactory;
        }

        @Bean
        @Override
        public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
            adapter.setIgnoreDefaultModelOnRedirect((this.mvcProperties != null)
                    ? this.mvcProperties.isIgnoreDefaultModelOnRedirect() : true);
            return adapter;
        }

        @Override
        protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
            if (this.mvcRegistrations != null
                    && this.mvcRegistrations.getRequestMappingHandlerAdapter() != null) {
                return this.mvcRegistrations.getRequestMappingHandlerAdapter();
            }
            return super.createRequestMappingHandlerAdapter();
        }

        @Bean
        @Primary
        @Override
        public RequestMappingHandlerMapping requestMappingHandlerMapping() {
            // Must be @Primary for MvcUriComponentsBuilder to work
            return super.requestMappingHandlerMapping();
        }

        @Bean
        @Override
        public Validator mvcValidator() {
            if (!ClassUtils.isPresent("javax.validation.Validator",
                    getClass().getClassLoader())) {
                return super.mvcValidator();
            }
            return WebMvcValidator.get(getApplicationContext(), getValidator());
        }

        @Override
        protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
            if (this.mvcRegistrations != null
                    && this.mvcRegistrations.getRequestMappingHandlerMapping() != null) {
                return this.mvcRegistrations.getRequestMappingHandlerMapping();
            }
            return super.createRequestMappingHandlerMapping();
        }

        @Override
        protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer() {
            try {
                return this.beanFactory.getBean(ConfigurableWebBindingInitializer.class);
            }
            catch (NoSuchBeanDefinitionException ex) {
                return super.getConfigurableWebBindingInitializer();
            }
        }

        @Override
        protected ExceptionHandlerExceptionResolver createExceptionHandlerExceptionResolver() {
            if (this.mvcRegistrations != null && this.mvcRegistrations
                    .getExceptionHandlerExceptionResolver() != null) {
                return this.mvcRegistrations.getExceptionHandlerExceptionResolver();
            }
            return super.createExceptionHandlerExceptionResolver();
        }

        @Override
        protected void configureHandlerExceptionResolvers(
                List<HandlerExceptionResolver> exceptionResolvers) {
            super.configureHandlerExceptionResolvers(exceptionResolvers);
            if (exceptionResolvers.isEmpty()) {
                addDefaultHandlerExceptionResolvers(exceptionResolvers);
            }
            if (this.mvcProperties.isLogResolvedException()) {
                for (HandlerExceptionResolver resolver : exceptionResolvers) {
                    if (resolver instanceof AbstractHandlerExceptionResolver) {
                        ((AbstractHandlerExceptionResolver) resolver)
                                .setWarnLogCategory(resolver.getClass().getName());
                    }
                }
            }
        }

        @Bean
        @Override
        public ContentNegotiationManager mvcContentNegotiationManager() {
            ContentNegotiationManager manager = super.mvcContentNegotiationManager();
            List<ContentNegotiationStrategy> strategies = manager.getStrategies();
            ListIterator<ContentNegotiationStrategy> iterator = strategies.listIterator();
            while (iterator.hasNext()) {
                ContentNegotiationStrategy strategy = iterator.next();
                if (strategy instanceof PathExtensionContentNegotiationStrategy) {
                    iterator.set(new OptionalPathExtensionContentNegotiationStrategy(
                            strategy));
                }
            }
            return manager;
        }

    }

    @Configuration
    @ConditionalOnEnabledResourceChain
    static class ResourceChainCustomizerConfiguration {

        @Bean
        public ResourceChainResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer() {
            return new ResourceChainResourceHandlerRegistrationCustomizer();
        }

    }

    interface ResourceHandlerRegistrationCustomizer {

        void customize(ResourceHandlerRegistration registration);

    }

    private static class ResourceChainResourceHandlerRegistrationCustomizer
            implements ResourceHandlerRegistrationCustomizer {

        @Autowired
        private ResourceProperties resourceProperties = new ResourceProperties();

        @Override
        public void customize(ResourceHandlerRegistration registration) {
            ResourceProperties.Chain properties = this.resourceProperties.getChain();
            configureResourceChain(properties,
                    registration.resourceChain(properties.isCache()));
        }

        private void configureResourceChain(ResourceProperties.Chain properties,
                                            ResourceChainRegistration chain) {
            Strategy strategy = properties.getStrategy();
            if (strategy.getFixed().isEnabled() || strategy.getContent().isEnabled()) {
                chain.addResolver(getVersionResourceResolver(strategy));
            }
            if (properties.isGzipped()) {
                chain.addResolver(new GzipResourceResolver());
            }
            if (properties.isHtmlApplicationCache()) {
                chain.addTransformer(new AppCacheManifestTransformer());
            }
        }

        private ResourceResolver getVersionResourceResolver(
                ResourceProperties.Strategy properties) {
            VersionResourceResolver resolver = new VersionResourceResolver();
            if (properties.getFixed().isEnabled()) {
                String version = properties.getFixed().getVersion();
                String[] paths = properties.getFixed().getPaths();
                resolver.addFixedVersionStrategy(version, paths);
            }
            if (properties.getContent().isEnabled()) {
                String[] paths = properties.getContent().getPaths();
                resolver.addContentVersionStrategy(paths);
            }
            return resolver;
        }

    }

    static final class WelcomePageHandlerMapping extends AbstractUrlHandlerMapping {

        private static final Log logger = LogFactory
                .getLog(WelcomePageHandlerMapping.class);

        private WelcomePageHandlerMapping(Resource welcomePage,
                                          String staticPathPattern) {
            if (welcomePage != null && "/**".equals(staticPathPattern)) {
                logger.info("Adding welcome page: " + welcomePage);
                ParameterizableViewController controller = new ParameterizableViewController();
                controller.setViewName("forward:index.html");
                setRootHandler(controller);
                setOrder(0);
            }
        }

        @Override
        public Object getHandlerInternal(HttpServletRequest request) throws Exception {
            for (MediaType mediaType : getAcceptedMediaTypes(request)) {
                if (mediaType.includes(MediaType.TEXT_HTML)) {
                    return super.getHandlerInternal(request);
                }
            }
            return null;
        }

        private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) {
            String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
            return MediaType.parseMediaTypes(
                    StringUtils.hasText(acceptHeader) ? acceptHeader : "*/*");
        }

    }

    /**
     * Decorator to make {@link PathExtensionContentNegotiationStrategy} optional
     * depending on a request attribute.
     */
    static class OptionalPathExtensionContentNegotiationStrategy
            implements ContentNegotiationStrategy {

        private static final String SKIP_ATTRIBUTE = PathExtensionContentNegotiationStrategy.class
                .getName() + ".SKIP";

        private final ContentNegotiationStrategy delegate;

        OptionalPathExtensionContentNegotiationStrategy(
                ContentNegotiationStrategy delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest)
                throws HttpMediaTypeNotAcceptableException {
            Object skip = webRequest.getAttribute(SKIP_ATTRIBUTE,
                    RequestAttributes.SCOPE_REQUEST);
            if (skip != null && Boolean.parseBoolean(skip.toString())) {
                return Collections.emptyList();
            }
            return this.delegate.resolveMediaTypes(webRequest);
        }

    }

}
