package com.rocket.summer.framework.web.util;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.Serializable;

/**
 * Servlet 2.3 HttpSessionListener that automatically exposes the
 * session mutex when an HttpSession gets created.
 * To be registered as a listener in <code>web.xml</code>.
 *
 * <p>The session mutex is guaranteed to be the same object during
 * the entire lifetime of the session, available under the key defined
 * by the <code>SESSION_MUTEX_ATTRIBUTE</code> constant. It serves as a
 * safe reference to synchronize on for locking on the current session.
 *
 * <p>In many cases, the HttpSession reference itself is a safe mutex
 * as well, since it will always be the same object reference for the
 * same active logical session. However, this is not guaranteed across
 * different servlet containers; the only 100% safe way is a session mutex.
 *
 * @author Juergen Hoeller
 * @since 1.2.7
 * @see WebUtils#SESSION_MUTEX_ATTRIBUTE
 * @see WebUtils#getSessionMutex(javax.servlet.http.HttpSession)
 * @see org.springframework.web.servlet.mvc.AbstractController#setSynchronizeOnSession
 */
public class HttpSessionMutexListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, new Mutex());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        event.getSession().removeAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);
    }


    /**
     * The mutex to be registered.
     * Doesn't need to be anything but a plain Object to synchronize on.
     * Should be serializable to allow for HttpSession persistence.
     */
    private static class Mutex implements Serializable {
    }

}

