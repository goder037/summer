package com.rocket.summer.framework.boot.web.support;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.boot.builder.ParentContextApplicationContextInitializer;
import com.rocket.summer.framework.boot.builder.SpringApplicationBuilder;
import com.rocket.summer.framework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import com.rocket.summer.framework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import com.rocket.summer.framework.boot.web.servlet.ServletContextInitializer;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.WebApplicationInitializer;
import com.rocket.summer.framework.web.context.ConfigurableWebEnvironment;
import com.rocket.summer.framework.web.context.ContextLoaderListener;
import com.rocket.summer.framework.web.context.WebApplicationContext;

/**
 * An opinionated {@link WebApplicationInitializer} to run a {@link SpringApplication}
 * from a traditional WAR deployment. Binds {@link Servlet}, {@link Filter} and
 * {@link ServletContextInitializer} beans from the application context to the servlet
 * container.
 * <p>
 * To configure the application either override the
 * {@link #configure(SpringApplicationBuilder)} method (calling
 * {@link SpringApplicationBuilder#sources(Object...)}) or make the initializer itself a
 * {@code @Configuration}. If you are using {@link SpringBootServletInitializer} in
 * combination with other {@link WebApplicationInitializer WebApplicationInitializers} you
 * might also want to add an {@code @Ordered} annotation to configure a specific startup
 * order.
 * <p>
 * Note that a WebApplicationInitializer is only needed if you are building a war file and
 * deploying it. If you prefer to run an embedded container then you won't need this at
 * all.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.4.0
 * @see #configure(SpringApplicationBuilder)
 */
public abstract class SpringBootServletInitializer implements WebApplicationInitializer {

    protected Log logger; // Don't initialize early

    private boolean registerErrorPageFilter = true;

    /**
     * Set if the {@link ErrorPageFilter} should be registered. Set to {@code false} if
     * error page mappings should be handled via the Servlet container and not Spring
     * Boot.
     * @param registerErrorPageFilter if the {@link ErrorPageFilter} should be registered.
     */
    protected final void setRegisterErrorPageFilter(boolean registerErrorPageFilter) {
        this.registerErrorPageFilter = registerErrorPageFilter;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Logger initialization is deferred in case a ordered
        // LogServletContextInitializer is being used
        this.logger = LogFactory.getLog(getClass());
        WebApplicationContext rootAppContext = createRootApplicationContext(
                servletContext);
        if (rootAppContext != null) {
            servletContext.addListener(new ContextLoaderListener(rootAppContext) {
                @Override
                public void contextInitialized(ServletContextEvent event) {
                    // no-op because the application context is already initialized
                }
            });
        }
        else {
            this.logger.debug("No ContextLoaderListener registered, as "
                    + "createRootApplicationContext() did not "
                    + "return an application context");
        }
    }

    protected WebApplicationContext createRootApplicationContext(
            ServletContext servletContext) {
        SpringApplicationBuilder builder = createSpringApplicationBuilder();
        builder.main(getClass());
        ApplicationContext parent = getExistingRootWebApplicationContext(servletContext);
        if (parent != null) {
            this.logger.info("Root context already created (using as parent).");
            servletContext.setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
            builder.initializers(new ParentContextApplicationContextInitializer(parent));
        }
        builder.initializers(
                new ServletContextApplicationContextInitializer(servletContext));
        builder.contextClass(AnnotationConfigEmbeddedWebApplicationContext.class);
        builder = configure(builder);
        builder.listeners(new WebEnvironmentPropertySourceInitializer(servletContext));
        SpringApplication application = builder.build();
        if (application.getSources().isEmpty() && AnnotationUtils
                .findAnnotation(getClass(), Configuration.class) != null) {
            application.getSources().add(getClass());
        }
        Assert.state(!application.getSources().isEmpty(),
                "No SpringApplication sources have been defined. Either override the "
                        + "configure method or add an @Configuration annotation");
        // Ensure error pages are registered
        if (this.registerErrorPageFilter) {
            application.getSources().add(ErrorPageFilterConfiguration.class);
        }
        return run(application);
    }

    /**
     * Returns the {@code SpringApplicationBuilder} that is used to configure and create
     * the {@link SpringApplication}. The default implementation returns a new
     * {@code SpringApplicationBuilder} in its default state.
     * @return the {@code SpringApplicationBuilder}.
     * @since 1.3.0
     */
    protected SpringApplicationBuilder createSpringApplicationBuilder() {
        return new SpringApplicationBuilder();
    }

    /**
     * Called to run a fully configured {@link SpringApplication}.
     * @param application the application to run
     * @return the {@link WebApplicationContext}
     */
    protected WebApplicationContext run(SpringApplication application) {
        return (WebApplicationContext) application.run();
    }

    private ApplicationContext getExistingRootWebApplicationContext(
            ServletContext servletContext) {
        Object context = servletContext.getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (context instanceof ApplicationContext) {
            return (ApplicationContext) context;
        }
        return null;
    }

    /**
     * Configure the application. Normally all you would need to do is to add sources
     * (e.g. config classes) because other settings have sensible defaults. You might
     * choose (for instance) to add default command line arguments, or set an active
     * Spring profile.
     * @param builder a builder for the application context
     * @return the application builder
     * @see SpringApplicationBuilder
     */
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder;
    }

    private static final class WebEnvironmentPropertySourceInitializer
            implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

        private final ServletContext servletContext;

        private WebEnvironmentPropertySourceInitializer(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
            ConfigurableEnvironment environment = event.getEnvironment();
            if (environment instanceof ConfigurableWebEnvironment) {
                ((ConfigurableWebEnvironment) environment)
                        .initPropertySources(this.servletContext, null);
            }
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

    }

}

