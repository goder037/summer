package com.rocket.summer.framework.boot.autoconfigure.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.aop.framework.autoproxy.AutoProxyUtils;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.ObjectProvider;
import com.rocket.summer.framework.beans.factory.config.BeanFactoryPostProcessor;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureBefore;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionMessage;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionOutcome;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import com.rocket.summer.framework.boot.autoconfigure.condition.SearchStrategy;
import com.rocket.summer.framework.boot.autoconfigure.condition.SpringBootCondition;
import com.rocket.summer.framework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import com.rocket.summer.framework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import com.rocket.summer.framework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.boot.web.servlet.ErrorPage;
import com.rocket.summer.framework.boot.web.servlet.ErrorPageRegistrar;
import com.rocket.summer.framework.boot.web.servlet.ErrorPageRegistry;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.expression.MapAccessor;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.Expression;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.expression.spel.support.SimpleEvaluationContext;
import com.rocket.summer.framework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import com.rocket.summer.framework.web.servlet.DispatcherServlet;
import com.rocket.summer.framework.web.servlet.View;
import com.rocket.summer.framework.web.servlet.view.BeanNameViewResolver;
import com.rocket.summer.framework.web.util.HtmlUtils;

/**
 * {@link EnableAutoConfiguration Auto-configuration} to render errors via an MVC error
 * controller.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
// Load before the main WebMvcAutoConfiguration so that the error View is available
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(ResourceProperties.class)
public class ErrorMvcAutoConfiguration {

    private final ServerProperties serverProperties;

    private final List<ErrorViewResolver> errorViewResolvers;

    public ErrorMvcAutoConfiguration(ServerProperties serverProperties,
                                     ObjectProvider<List<ErrorViewResolver>> errorViewResolversProvider) {
        this.serverProperties = serverProperties;
        this.errorViewResolvers = errorViewResolversProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class,
            search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class,
            search = SearchStrategy.CURRENT)
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes) {
        return new BasicErrorController(errorAttributes, this.serverProperties.getError(),
                this.errorViewResolvers);
    }

    @Bean
    public ErrorPageCustomizer errorPageCustomizer() {
        return new ErrorPageCustomizer(this.serverProperties);
    }

    @Bean
    public static PreserveErrorControllerTargetClassPostProcessor preserveErrorControllerTargetClassPostProcessor() {
        return new PreserveErrorControllerTargetClassPostProcessor();
    }

    @Configuration
    static class DefaultErrorViewResolverConfiguration {

        private final ApplicationContext applicationContext;

        private final ResourceProperties resourceProperties;

        DefaultErrorViewResolverConfiguration(ApplicationContext applicationContext,
                                              ResourceProperties resourceProperties) {
            this.applicationContext = applicationContext;
            this.resourceProperties = resourceProperties;
        }

        @Bean
        @ConditionalOnBean(DispatcherServlet.class)
        @ConditionalOnMissingBean
        public DefaultErrorViewResolver conventionErrorViewResolver() {
            return new DefaultErrorViewResolver(this.applicationContext,
                    this.resourceProperties);
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "server.error.whitelabel", name = "enabled",
            matchIfMissing = true)
    @Conditional(ErrorTemplateMissingCondition.class)
    protected static class WhitelabelErrorViewConfiguration {

        private final SpelView defaultErrorView = new SpelView(
                "<html><body><h1>Whitelabel Error Page</h1>"
                        + "<p>This application has no explicit mapping for /error, so you are seeing this as a fallback.</p>"
                        + "<div id='created'>${timestamp}</div>"
                        + "<div>There was an unexpected error (type=${error}, status=${status}).</div>"
                        + "<div>${message}</div></body></html>");

        @Bean(name = "error")
        @ConditionalOnMissingBean(name = "error")
        public View defaultErrorView() {
            return this.defaultErrorView;
        }

        // If the user adds @EnableWebMvc then the bean name view resolver from
        // WebMvcAutoConfiguration disappears, so add it back in to avoid disappointment.
        @Bean
        @ConditionalOnMissingBean(BeanNameViewResolver.class)
        public BeanNameViewResolver beanNameViewResolver() {
            BeanNameViewResolver resolver = new BeanNameViewResolver();
            resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
            return resolver;
        }

    }

    /**
     * {@link SpringBootCondition} that matches when no error template view is detected.
     */
    private static class ErrorTemplateMissingCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context,
                                                AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage
                    .forCondition("ErrorTemplate Missing");
            TemplateAvailabilityProviders providers = new TemplateAvailabilityProviders(
                    context.getClassLoader());
            TemplateAvailabilityProvider provider = providers.getProvider("error",
                    context.getEnvironment(), context.getClassLoader(),
                    context.getResourceLoader());
            if (provider != null) {
                return ConditionOutcome
                        .noMatch(message.foundExactly("template from " + provider));
            }
            return ConditionOutcome
                    .match(message.didNotFind("error template view").atAll());
        }

    }

    /**
     * Simple {@link View} implementation that resolves variables as SpEL expressions.
     */
    private static class SpelView implements View {

        private final NonRecursivePropertyPlaceholderHelper helper;

        private final String template;

        private volatile Map<String, Expression> expressions;

        SpelView(String template) {
            this.helper = new NonRecursivePropertyPlaceholderHelper("${", "}");
            this.template = template;
        }

        @Override
        public String getContentType() {
            return "text/html";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
            if (response.getContentType() == null) {
                response.setContentType(getContentType());
            }
            Map<String, Object> map = new HashMap<String, Object>(model);
            map.put("path", request.getContextPath());
            PlaceholderResolver resolver = new ExpressionResolver(getExpressions(), map);
            String result = this.helper.replacePlaceholders(this.template, resolver);
            response.getWriter().append(result);
        }

        private Map<String, Expression> getExpressions() {
            if (this.expressions == null) {
                synchronized (this) {
                    ExpressionCollector expressionCollector = new ExpressionCollector();
                    this.helper.replacePlaceholders(this.template, expressionCollector);
                    this.expressions = expressionCollector.getExpressions();
                }
            }
            return this.expressions;
        }

    }

    /**
     * {@link PlaceholderResolver} to collect placeholder expressions.
     */
    private static class ExpressionCollector implements PlaceholderResolver {

        private final SpelExpressionParser parser = new SpelExpressionParser();

        private final Map<String, Expression> expressions = new HashMap<String, Expression>();

        @Override
        public String resolvePlaceholder(String name) {
            this.expressions.put(name, this.parser.parseExpression(name));
            return null;
        }

        public Map<String, Expression> getExpressions() {
            return Collections.unmodifiableMap(this.expressions);
        }

    }

    /**
     * SpEL based {@link PlaceholderResolver}.
     */
    private static class ExpressionResolver implements PlaceholderResolver {

        private final Map<String, Expression> expressions;

        private final EvaluationContext context;

        ExpressionResolver(Map<String, Expression> expressions, Map<String, ?> map) {
            this.expressions = expressions;
            this.context = getContext(map);
        }

        private EvaluationContext getContext(Map<String, ?> map) {
            return SimpleEvaluationContext.forPropertyAccessors(new MapAccessor())
                    .withRootObject(map).build();
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            Expression expression = this.expressions.get(placeholderName);
            return escape(
                    (expression != null) ? expression.getValue(this.context) : null);
        }

        private String escape(Object value) {
            return HtmlUtils.htmlEscape((value != null) ? value.toString() : null);
        }

    }

    /**
     * {@link EmbeddedServletContainerCustomizer} that configures the container's error
     * pages.
     */
    private static class ErrorPageCustomizer implements ErrorPageRegistrar, Ordered {

        private final ServerProperties properties;

        protected ErrorPageCustomizer(ServerProperties properties) {
            this.properties = properties;
        }

        @Override
        public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
            ErrorPage errorPage = new ErrorPage(this.properties.getServletPrefix()
                    + this.properties.getError().getPath());
            errorPageRegistry.addErrorPages(errorPage);
        }

        @Override
        public int getOrder() {
            return 0;
        }

    }

    /**
     * {@link BeanFactoryPostProcessor} to ensure that the target class of ErrorController
     * MVC beans are preserved when using AOP.
     */
    static class PreserveErrorControllerTargetClassPostProcessor
            implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
            String[] errorControllerBeans = beanFactory
                    .getBeanNamesForType(ErrorController.class, false, false);
            for (String errorControllerBean : errorControllerBeans) {
                try {
                    beanFactory.getBeanDefinition(errorControllerBean).setAttribute(
                            AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
                }
                catch (Throwable ex) {
                    // Ignore
                }
            }
        }

    }

}

