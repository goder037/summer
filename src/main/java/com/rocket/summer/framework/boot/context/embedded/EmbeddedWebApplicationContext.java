package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.web.context.WebApplicationContext;
import com.rocket.summer.framework.web.context.support.GenericWebApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

/**
 * A {@link WebApplicationContext} that can be used to bootstrap itself from a contained
 * {@link EmbeddedServletContainerFactory} bean.
 * <p>
 * This context will create, initialize and run an {@link EmbeddedServletContainer} by
 * searching for a single {@link EmbeddedServletContainerFactory} bean within the
 * {@link ApplicationContext} itself. The {@link EmbeddedServletContainerFactory} is free
 * to use standard Spring concepts (such as dependency injection, lifecycle callbacks and
 * property placeholder variables).
 * <p>
 * In addition, any {@link Servlet} or {@link Filter} beans defined in the context will be
 * automatically registered with the embedded Servlet container. In the case of a single
 * Servlet bean, the '/' mapping will be used. If multiple Servlet beans are found then
 * the lowercase bean name will be used as a mapping prefix. Any Servlet named
 * 'dispatcherServlet' will always be mapped to '/'. Filter beans will be mapped to all
 * URLs ('/*').
 * <p>
 * For more advanced configuration, the context can instead define beans that implement
 * the {@link ServletContextInitializer} interface (most often
 * {@link ServletRegistrationBean}s and/or {@link FilterRegistrationBean}s). To prevent
 * double registration, the use of {@link ServletContextInitializer} beans will disable
 * automatic Servlet and Filter bean registration.
 * <p>
 * Although this context can be used directly, most developers should consider using the
 * {@link AnnotationConfigEmbeddedWebApplicationContext} or
 * {@link XmlEmbeddedWebApplicationContext} variants.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @see AnnotationConfigEmbeddedWebApplicationContext
 * @see XmlEmbeddedWebApplicationContext
 * @see EmbeddedServletContainerFactory
 */
public class EmbeddedWebApplicationContext extends GenericWebApplicationContext {

    private static final Log logger = LogFactory
            .getLog(EmbeddedWebApplicationContext.class);

    /**
     * Constant value for the DispatcherServlet bean name. A Servlet bean with this name
     * is deemed to be the "main" servlet and is automatically given a mapping of "/" by
     * default. To change the default behaviour you can use a
     * {@link ServletRegistrationBean} or a different bean name.
     */
    public static final String DISPATCHER_SERVLET_NAME = "dispatcherServlet";

    private volatile EmbeddedServletContainer embeddedServletContainer;

    private ServletConfig servletConfig;

    private String namespace;
}
