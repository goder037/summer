package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashSet;
import java.util.Set;

/**
 * HttpServletRequest decorator that makes all Spring beans in a
 * given WebApplicationContext accessible as request attributes,
 * through lazy checking once an attribute gets accessed.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class ContextExposingHttpServletRequest extends HttpServletRequestWrapper {

    private final WebApplicationContext webApplicationContext;

    private final Set<String> exposedContextBeanNames;

    private Set<String> explicitAttributes;


    /**
     * Create a new ContextExposingHttpServletRequest for the given request.
     * @param originalRequest the original HttpServletRequest
     * @param context the WebApplicationContext that this request runs in
     */
    public ContextExposingHttpServletRequest(HttpServletRequest originalRequest, WebApplicationContext context) {
        this(originalRequest, context, null);
    }

    /**
     * Create a new ContextExposingHttpServletRequest for the given request.
     * @param originalRequest the original HttpServletRequest
     * @param context the WebApplicationContext that this request runs in
     * @param exposedContextBeanNames the names of beans in the context which
     * are supposed to be exposed (if this is non-null, only the beans in this
     * Set are eligible for exposure as attributes)
     */
    public ContextExposingHttpServletRequest(
            HttpServletRequest originalRequest, WebApplicationContext context, Set<String> exposedContextBeanNames) {

        super(originalRequest);
        Assert.notNull(context, "WebApplicationContext must not be null");
        this.webApplicationContext = context;
        this.exposedContextBeanNames = exposedContextBeanNames;
    }


    /**
     * Return the WebApplicationContext that this request runs in.
     */
    public final WebApplicationContext getWebApplicationContext() {
        return this.webApplicationContext;
    }


    @Override
    public Object getAttribute(String name) {
        if ((this.explicitAttributes == null || !this.explicitAttributes.contains(name)) &&
                (this.exposedContextBeanNames == null || this.exposedContextBeanNames.contains(name)) &&
                this.webApplicationContext.containsBean(name)) {
            return this.webApplicationContext.getBean(name);
        }
        else {
            return super.getAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, Object value) {
        super.setAttribute(name, value);
        if (this.explicitAttributes == null) {
            this.explicitAttributes = new HashSet<String>(8);
        }
        this.explicitAttributes.add(name);
    }

}

