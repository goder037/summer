package com.rocket.summer.framework.web.servlet;

import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * A {@link ServletContextInitializer} to register {@link EventListener}s in a Servlet
 * 3.0+ container. Similar to the {@link ServletContext#addListener(EventListener)
 * registration} features provided by {@link ServletContext} but with a Spring Bean
 * friendly design.
 *
 * This bean can be used to register the following types of listener:
 * <ul>
 * <li>{@link ServletContextAttributeListener}</li>
 * <li>{@link ServletRequestListener}</li>
 * <li>{@link ServletRequestAttributeListener}</li>
 * <li>{@link HttpSessionAttributeListener}</li>
 * <li>{@link HttpSessionListener}</li>
 * <li>{@link ServletContextListener}</li>
 * </ul>
 *
 * @param <T> the type of listener
 * @author Dave Syer
 * @author Phillip Webb
 * @since 1.4.0
 */
public class ServletListenerRegistrationBean<T extends EventListener>
        extends RegistrationBean {

    private static final Log logger = LogFactory
            .getLog(ServletListenerRegistrationBean.class);

    private static final Set<Class<?>> SUPPORTED_TYPES;

    static {
        Set<Class<?>> types = new HashSet<Class<?>>();
        types.add(ServletContextAttributeListener.class);
        types.add(ServletRequestListener.class);
        types.add(ServletRequestAttributeListener.class);
        types.add(HttpSessionAttributeListener.class);
        types.add(HttpSessionListener.class);
        types.add(ServletContextListener.class);
        SUPPORTED_TYPES = Collections.unmodifiableSet(types);
    }

    private T listener;

    /**
     * Create a new {@link ServletListenerRegistrationBean} instance.
     */
    public ServletListenerRegistrationBean() {
    }

    /**
     * Create a new {@link ServletListenerRegistrationBean} instance.
     * @param listener the listener to register
     */
    public ServletListenerRegistrationBean(T listener) {
        Assert.notNull(listener, "Listener must not be null");
        Assert.isTrue(isSupportedType(listener), "Listener is not of a supported type");
        this.listener = listener;
    }

    /**
     * Set the listener that will be registered.
     * @param listener the listener to register
     */
    public void setListener(T listener) {
        Assert.notNull(listener, "Listener must not be null");
        Assert.isTrue(isSupportedType(listener), "Listener is not of a supported type");
        this.listener = listener;
    }

    /**
     * Set the name of this registration. If not specified the bean name will be used.
     * @param name the name of the registration
     * @deprecated as of 1.5 since not applicable to listeners
     */
    @Override
    @Deprecated
    public void setName(String name) {
        super.setName(name);
    }

    /**
     * Sets if asynchronous operations are support for this registration. If not specified
     * defaults to {@code true}.
     * @param asyncSupported if async is supported
     * @deprecated as of 1.5 since not applicable to listeners
     */
    @Override
    @Deprecated
    public void setAsyncSupported(boolean asyncSupported) {
        super.setAsyncSupported(asyncSupported);
    }

    /**
     * Returns if asynchronous operations are support for this registration.
     * @return if async is supported
     * @deprecated as of 1.5 since not applicable to listeners
     */
    @Override
    @Deprecated
    public boolean isAsyncSupported() {
        return super.isAsyncSupported();
    }

    /**
     * Set init-parameters for this registration. Calling this method will replace any
     * existing init-parameters.
     * @param initParameters the init parameters
     * @deprecated as of 1.5 since not applicable to listeners
     */
    @Override
    @Deprecated
    public void setInitParameters(Map<String, String> initParameters) {
        super.setInitParameters(initParameters);
    }

    /**
     * Returns a mutable Map of the registration init-parameters.
     * @return the init parameters
     * @deprecated as of 1.5 since not applicable to listeners
     */
    @Override
    @Deprecated
    public Map<String, String> getInitParameters() {
        return super.getInitParameters();
    }

    /**
     * Add a single init-parameter, replacing any existing parameter with the same name.
     * @param name the init-parameter name
     * @param value the init-parameter value
     * @deprecated as of 1.5 since not applicable to listeners
     */
    @Override
    @Deprecated
    public void addInitParameter(String name, String value) {
        super.addInitParameter(name, value);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (!isEnabled()) {
            logger.info("Listener " + this.listener + " was not registered (disabled)");
            return;
        }
        try {
            servletContext.addListener(this.listener);
        }
        catch (RuntimeException ex) {
            throw new IllegalStateException(
                    "Failed to add listener '" + this.listener + "' to servlet context",
                    ex);
        }
    }

    public T getListener() {
        return this.listener;
    }

    /**
     * Returns {@code true} if the specified listener is one of the supported types.
     * @param listener the listener to test
     * @return if the listener is of a supported type
     */
    public static boolean isSupportedType(EventListener listener) {
        for (Class<?> type : SUPPORTED_TYPES) {
            if (ClassUtils.isAssignableValue(type, listener)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the supported types for this registration.
     * @return the supported types
     */
    public static Set<Class<?>> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

}
