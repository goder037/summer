package com.rocket.summer.framework.web.context.request;

import com.rocket.summer.framework.core.NamedInheritableThreadLocal;
import com.rocket.summer.framework.core.NamedThreadLocal;

/**
 * Holder class to expose the web request in the form of a thread-bound
 * {@link RequestAttributes} object. The request will be inherited
 * by any child threads spawned by the current thread if the
 * <code>inheritable<code> flag is set to <code>true</code>.
 *
 * <p>Use {@link RequestContextListener} or
 * {@link org.springframework.web.filter.RequestContextFilter} to expose
 * the current web request. Note that
 * {@link org.springframework.web.servlet.DispatcherServlet} and
 * {@link org.springframework.web.portlet.DispatcherPortlet} already
 * expose the current request by default.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 2.0
 * @see RequestContextListener
 * @see org.springframework.web.filter.RequestContextFilter
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.portlet.DispatcherPortlet
 */
public abstract class RequestContextHolder  {

    private static final ThreadLocal<RequestAttributes> requestAttributesHolder =
            new NamedThreadLocal<RequestAttributes>("Request attributes");

    private static final ThreadLocal<RequestAttributes> inheritableRequestAttributesHolder =
            new NamedInheritableThreadLocal<RequestAttributes>("Request context");


    /**
     * Reset the RequestAttributes for the current thread.
     */
    public static void resetRequestAttributes() {
        requestAttributesHolder.remove();
        inheritableRequestAttributesHolder.remove();
    }

    /**
     * Bind the given RequestAttributes to the current thread,
     * <i>not</i> exposing it as inheritable for child threads.
     * @param attributes the RequestAttributes to expose
     * @see #setRequestAttributes(RequestAttributes, boolean)
     */
    public static void setRequestAttributes(RequestAttributes attributes) {
        setRequestAttributes(attributes, false);
    }

    /**
     * Bind the given RequestAttributes to the current thread.
     * @param attributes the RequestAttributes to expose,
     * or <code>null</code> to reset the thread-bound context
     * @param inheritable whether to expose the RequestAttributes as inheritable
     * for child threads (using an {@link java.lang.InheritableThreadLocal})
     */
    public static void setRequestAttributes(RequestAttributes attributes, boolean inheritable) {
        if (attributes == null) {
            resetRequestAttributes();
        }
        else {
            if (inheritable) {
                inheritableRequestAttributesHolder.set(attributes);
                requestAttributesHolder.remove();
            }
            else {
                requestAttributesHolder.set(attributes);
                inheritableRequestAttributesHolder.remove();
            }
        }
    }

    /**
     * Return the RequestAttributes currently bound to the thread.
     * @return the RequestAttributes currently bound to the thread,
     * or <code>null</code> if none bound
     */
    public static RequestAttributes getRequestAttributes() {
        RequestAttributes attributes = requestAttributesHolder.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributesHolder.get();
        }
        return attributes;
    }

    /**
     * Return the RequestAttributes currently bound to the thread.
     * <p>Exposes the previously bound RequestAttributes instance, if any.
     * Falls back to the current JSF FacesContext, if any.
     * @return the RequestAttributes currently bound to the thread
     * @throws IllegalStateException if no RequestAttributes object
     * is bound to the current thread
     * @see #setRequestAttributes
     * @see ServletRequestAttributes
     */
    public static RequestAttributes currentRequestAttributes() throws IllegalStateException {
        RequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No thread-bound request found: " +
                    "Are you referring to request attributes outside of an actual web request, " +
                    "or processing a request outside of the originally receiving thread? " +
                    "If you are actually operating within a web request and still receive this message, " +
                    "your code is probably running outside of DispatcherServlet/DispatcherPortlet: " +
                    "In this case, use RequestContextListener or RequestContextFilter to expose the current request.");
        }
        return attributes;
    }

}

