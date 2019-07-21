package com.rocket.summer.framework.web.context.request;

import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.beans.factory.ObjectFactory;
import com.rocket.summer.framework.beans.factory.config.Scope;
import com.rocket.summer.framework.util.Assert;

import javax.servlet.ServletContext;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link Scope} wrapper for a ServletContext, i.e. for global web application attributes.
 *
 * <p>This differs from traditional Spring singletons in that it exposes attributes in the
 * ServletContext. Those attributes will get destroyed whenever the entire application
 * shuts down, which might be earlier or later than the shutdown of the containing Spring
 * ApplicationContext.
 *
 * <p>The associated destruction mechanism relies on a
 * {@link org.springframework.web.context.ContextCleanupListener} being registered in
 * <code>web.xml</code>. Note that {@link org.springframework.web.context.ContextLoaderListener}
 * includes ContextCleanupListener's functionality.
 *
 * <p>This scope is registered as default scope with key
 * {@link org.springframework.web.context.WebApplicationContext#SCOPE_APPLICATION "application"}.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.web.context.ContextCleanupListener
 */
public class ServletContextScope implements Scope, DisposableBean {

    private final ServletContext servletContext;

    private final Map<String, Runnable> destructionCallbacks = new LinkedHashMap<String, Runnable>();


    /**
     * Create a new Scope wrapper for the given ServletContext.
     * @param servletContext the ServletContext to wrap
     */
    public ServletContextScope(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext must not be null");
        this.servletContext = servletContext;
    }

    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object scopedObject = this.servletContext.getAttribute(name);
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            this.servletContext.setAttribute(name, scopedObject);
        }
        return scopedObject;
    }

    public Object remove(String name) {
        Object scopedObject = this.servletContext.getAttribute(name);
        if (scopedObject != null) {
            this.servletContext.removeAttribute(name);
            this.destructionCallbacks.remove(name);
            return scopedObject;
        }
        else {
            return null;
        }
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        this.destructionCallbacks.put(name, callback);
    }

    public Object resolveContextualObject(String key) {
        return null;
    }

    public String getConversationId() {
        return null;
    }


    /**
     * Invoke all registered destruction callbacks.
     * To be called on ServletContext shutdown.
     * @see org.springframework.web.context.ContextCleanupListener
     */
    public void destroy() {
        for (Runnable runnable : this.destructionCallbacks.values()) {
            runnable.run();
        }
        this.destructionCallbacks.clear();
    }

}

