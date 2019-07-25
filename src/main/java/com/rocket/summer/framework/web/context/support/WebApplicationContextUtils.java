package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.beans.factory.ObjectFactory;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.context.support.ServletConfigPropertySource;
import com.rocket.summer.framework.context.support.ServletContextPropertySource;
import com.rocket.summer.framework.core.env.MutablePropertySources;
import com.rocket.summer.framework.core.env.PropertySource;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.ConfigurableWebApplicationContext;
import com.rocket.summer.framework.web.context.WebApplicationContext;
import com.rocket.summer.framework.web.context.request.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.rocket.summer.framework.web.context.support.StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME;
import static com.rocket.summer.framework.web.context.support.StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME;

/**
 * Convenience methods for retrieving the root
 * {@link org.springframework.web.context.WebApplicationContext} for a given
 * <code>ServletContext</code>. This is e.g. useful for accessing a Spring
 * context from within custom web views or Struts actions.
 *
 * <p>Note that there are more convenient ways of accessing the root context for
 * many web frameworks, either part of Spring or available as external library.
 * This helper class is just the most generic way to access the root context.
 *
 * @author Juergen Hoeller
 * @see org.springframework.web.context.ContextLoader
 * @see org.springframework.web.servlet.FrameworkServlet
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.jsf.FacesContextUtils
 * @see org.springframework.web.jsf.SpringBeanVariableResolver
 * @see org.springframework.web.jsf.el.SpringBeanFacesELResolver
 */
public abstract class WebApplicationContextUtils {

    /**
     * Find the root WebApplicationContext for this web application, which is
     * typically loaded via {@link org.springframework.web.context.ContextLoaderListener}.
     * <p>Will rethrow an exception that happened on root context startup,
     * to differentiate between a failed context startup and no context at all.
     * @param sc ServletContext to find the web application context for
     * @return the root WebApplicationContext for this web app
     * @throws IllegalStateException if the root WebApplicationContext could not be found
     * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
     */
    public static WebApplicationContext getRequiredWebApplicationContext(ServletContext sc)
            throws IllegalStateException {

        WebApplicationContext wac = getWebApplicationContext(sc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }

    /**
     * Find the root WebApplicationContext for this web application, which is
     * typically loaded via {@link org.springframework.web.context.ContextLoaderListener}.
     * <p>Will rethrow an exception that happened on root context startup,
     * to differentiate between a failed context startup and no context at all.
     * @param sc ServletContext to find the web application context for
     * @return the root WebApplicationContext for this web app, or <code>null</code> if none
     * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
     */
    public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
        return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    /**
     * Replace {@code Servlet}-based stub property sources with actual instances
     * populated with the given context and config objects.
     * @see org.springframework.core.env.PropertySource.StubPropertySource
     * @see org.springframework.web.context.support.WebApplicationContextUtils#initServletPropertySources(MutablePropertySources, ServletContext)
     * @see org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
     */
    public static void initServletPropertySources(
            MutablePropertySources propertySources, ServletContext servletContext, ServletConfig servletConfig) {
        Assert.notNull(propertySources, "propertySources must not be null");
        if(servletContext != null &&
                propertySources.contains(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME) &&
                propertySources.get(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME) instanceof PropertySource.StubPropertySource) {
            propertySources.replace(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME, new ServletContextPropertySource(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME, servletContext));
        }
        if(servletConfig != null &&
                propertySources.contains(SERVLET_CONFIG_PROPERTY_SOURCE_NAME) &&
                propertySources.get(SERVLET_CONFIG_PROPERTY_SOURCE_NAME) instanceof PropertySource.StubPropertySource) {
            propertySources.replace(SERVLET_CONFIG_PROPERTY_SOURCE_NAME, new ServletConfigPropertySource(SERVLET_CONFIG_PROPERTY_SOURCE_NAME, servletConfig));
        }
    }

    /**
     * Find a custom WebApplicationContext for this web application.
     * @param sc ServletContext to find the web application context for
     * @param attrName the name of the ServletContext attribute to look for
     * @return the desired WebApplicationContext for this web app, or <code>null</code> if none
     */
    public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
        Assert.notNull(sc, "ServletContext must not be null");
        Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw (RuntimeException) attr;
        }
        if (attr instanceof Error) {
            throw (Error) attr;
        }
        if (attr instanceof Exception) {
            throw new IllegalStateException((Exception) attr);
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
        }
        return (WebApplicationContext) attr;
    }


    /**
     * Register web-specific scopes ("request", "session", "globalSession")
     * with the given BeanFactory, as used by the WebApplicationContext.
     * @param beanFactory the BeanFactory to configure
     */
    public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory) {
        registerWebApplicationScopes(beanFactory, null);
    }

    /**
     * Register web-specific scopes ("request", "session", "globalSession", "application")
     * with the given BeanFactory, as used by the WebApplicationContext.
     * @param beanFactory the BeanFactory to configure
     * @param sc the ServletContext that we're running within
     */
    public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory, ServletContext sc) {
        beanFactory.registerScope(WebApplicationContext.SCOPE_REQUEST, new RequestScope());
        beanFactory.registerScope(WebApplicationContext.SCOPE_SESSION, new SessionScope(false));
        beanFactory.registerScope(WebApplicationContext.SCOPE_GLOBAL_SESSION, new SessionScope(true));
        if (sc != null) {
            ServletContextScope appScope = new ServletContextScope(sc);
            beanFactory.registerScope(WebApplicationContext.SCOPE_APPLICATION, appScope);
            // Register as ServletContext attribute, for ContextCleanupListener to detect it.
            sc.setAttribute(ServletContextScope.class.getName(), appScope);
        }

        beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
        beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
        beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
    }

    /**
     * Register web-specific environment beans ("contextParameters", "contextAttributes")
     * with the given BeanFactory, as used by the WebApplicationContext.
     * @param bf the BeanFactory to configure
     * @param sc the ServletContext that we're running within
     */
    public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, ServletContext sc) {
        registerEnvironmentBeans(bf, sc, null);
    }

    /**
     * Register web-specific environment beans ("contextParameters", "contextAttributes")
     * with the given BeanFactory, as used by the WebApplicationContext.
     * @param bf the BeanFactory to configure
     * @param sc the ServletContext that we're running within
     * @param config the ServletConfig of the containing Portlet
     */
    public static void registerEnvironmentBeans(
            ConfigurableListableBeanFactory bf, ServletContext sc, ServletConfig config) {

        if (sc != null && !bf.containsBean(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME)) {
            bf.registerSingleton(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME, sc);
        }

        if (config != null && !bf.containsBean(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME)) {
            bf.registerSingleton(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME, config);
        }

        if (!bf.containsBean(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME)) {
            Map<String, String> parameterMap = new HashMap<String, String>();
            if (sc != null) {
                Enumeration paramNameEnum = sc.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    String paramName = (String) paramNameEnum.nextElement();
                    parameterMap.put(paramName, sc.getInitParameter(paramName));
                }
            }
            if (config != null) {
                Enumeration paramNameEnum = config.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    String paramName = (String) paramNameEnum.nextElement();
                    parameterMap.put(paramName, config.getInitParameter(paramName));
                }
            }
            bf.registerSingleton(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME,
                    Collections.unmodifiableMap(parameterMap));
        }

        if (!bf.containsBean(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME)) {
            Map<String, Object> attributeMap = new HashMap<String, Object>();
            if (sc != null) {
                Enumeration attrNameEnum = sc.getAttributeNames();
                while (attrNameEnum.hasMoreElements()) {
                    String attrName = (String) attrNameEnum.nextElement();
                    attributeMap.put(attrName, sc.getAttribute(attrName));
                }
            }
            bf.registerSingleton(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME,
                    Collections.unmodifiableMap(attributeMap));
        }
    }

    /**
     * Return the current RequestAttributes instance as ServletRequestAttributes.
     * @see RequestContextHolder#currentRequestAttributes()
     */
    private static ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
        if (!(requestAttr instanceof ServletRequestAttributes)) {
            throw new IllegalStateException("Current request is not a servlet request");
        }
        return (ServletRequestAttributes) requestAttr;
    }


    /**
     * Factory that exposes the current request object on demand.
     */
    private static class RequestObjectFactory implements ObjectFactory<ServletRequest>, Serializable {

        public ServletRequest getObject() {
            return currentRequestAttributes().getRequest();
        }

        @Override
        public String toString() {
            return "Current HttpServletRequest";
        }
    }


    /**
     * Factory that exposes the current session object on demand.
     */
    private static class SessionObjectFactory implements ObjectFactory<HttpSession>, Serializable {

        public HttpSession getObject() {
            return currentRequestAttributes().getRequest().getSession();
        }

        @Override
        public String toString() {
            return "Current HttpSession";
        }
    }


    /**
     * Factory that exposes the current WebRequest object on demand.
     */
    private static class WebRequestObjectFactory implements ObjectFactory<WebRequest>, Serializable {

        public WebRequest getObject() {
            return new ServletWebRequest(currentRequestAttributes().getRequest());
        }

        @Override
        public String toString() {
            return "Current ServletWebRequest";
        }
    }


}