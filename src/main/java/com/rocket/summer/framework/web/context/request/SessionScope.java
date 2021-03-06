package com.rocket.summer.framework.web.context.request;

import com.rocket.summer.framework.beans.factory.ObjectFactory;

/**
 * Session-backed {@link com.rocket.summer.framework.beans.factory.config.Scope}
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
 * @see RequestAttributes#SCOPE_SESSION
 * @see RequestAttributes#SCOPE_GLOBAL_SESSION
 * @see RequestContextListener
 * @see com.rocket.summer.framework.web.filter.RequestContextFilter
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 * @see com.rocket.summer.framework.web.portlet.DispatcherPortlet
 */
public class SessionScope extends AbstractRequestAttributesScope {

    private final int scope;


    /**
     * Create a new SessionScope, storing attributes in a locally
     * isolated session (or default session, if there is no distinction
     * between a global session and a component-specific session).
     */
    public SessionScope() {
        this.scope = RequestAttributes.SCOPE_SESSION;
    }

    /**
     * Create a new SessionScope, specifying whether to store attributes
     * in the global session, provided that such a distinction is available.
     * <p>This distinction is important for Portlet environments, where there
     * are two notions of a session: "portlet scope" and "application scope".
     * If this flag is on, objects will be put into the "application scope" session;
     * else they will end up in the "portlet scope" session (the typical default).
     * <p>In a Servlet environment, this flag is effectively ignored.
     * @param globalSession <code>true</code> in case of the global session as target;
     * <code>false</code> in case of a component-specific session as target
     * @see com.rocket.summer.framework.web.portlet.context.PortletRequestAttributes
     * @see com.rocket.summer.framework.web.context.request.ServletRequestAttributes
     */
    public SessionScope(boolean globalSession) {
        this.scope = (globalSession ? RequestAttributes.SCOPE_GLOBAL_SESSION : RequestAttributes.SCOPE_SESSION);
    }


    @Override
    protected int getScope() {
        return this.scope;
    }

    public String getConversationId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    @Override
    public Object get(String name, ObjectFactory objectFactory) {
        Object mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
        synchronized (mutex) {
            return super.get(name, objectFactory);
        }
    }

    @Override
    public Object remove(String name) {
        Object mutex = RequestContextHolder.currentRequestAttributes().getSessionMutex();
        synchronized (mutex) {
            return super.remove(name);
        }
    }

}
