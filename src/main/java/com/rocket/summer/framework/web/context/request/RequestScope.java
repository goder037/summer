package com.rocket.summer.framework.web.context.request;

/**
 * Request-backed {@link com.rocket.summer.framework.beans.factory.config.Scope}
 * implementation.
 *
 * <p>Relies on a thread-bound {@link RequestAttributes} instance, which
 * can be exported through {@link RequestContextListener},
 * {@link com.rocket.summer.framework.web.filter.RequestContextFilter} or
 * {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}.
 *
 * <p>This <code>Scope</code> will also work for Portlet environments,
 * through an alternate <code>RequestAttributes</code> implementation
 * (as exposed out-of-the-box by Spring's
 * {@link com.rocket.summer.framework.web.portlet.DispatcherPortlet}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see RequestContextHolder#currentRequestAttributes()
 * @see RequestAttributes#SCOPE_REQUEST
 * @see RequestContextListener
 * @see com.rocket.summer.framework.web.filter.RequestContextFilter
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 * @see com.rocket.summer.framework.web.portlet.DispatcherPortlet
 */
public class RequestScope extends AbstractRequestAttributesScope {

    @Override
    protected int getScope() {
        return RequestAttributes.SCOPE_REQUEST;
    }

    /**
     * There is no conversation id concept for a request, so this method
     * returns <code>null</code>.
     */
    public String getConversationId() {
        return null;
    }

}
