package com.rocket.summer.framework.web.filter;

import com.rocket.summer.framework.context.i18n.LocaleContextHolder;
import com.rocket.summer.framework.web.context.request.RequestContextHolder;
import com.rocket.summer.framework.web.context.request.ServletRequestAttributes;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter that exposes the request to the current thread,
 * through both {@link com.rocket.summer.framework.context.i18n.LocaleContextHolder} and
 * {@link RequestContextHolder}. To be registered as filter in {@code web.xml}.
 *
 * <p>Alternatively, Spring's {@link com.rocket.summer.framework.web.context.request.RequestContextListener}
 * and Spring's {@link com.rocket.summer.framework.web.servlet.DispatcherServlet} also expose
 * the same request context to the current thread.
 *
 * <p>This filter is mainly for use with third-party servlets, e.g. the JSF FacesServlet.
 * Within Spring's own web support, DispatcherServlet's processing is perfectly sufficient.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Rossen Stoyanchev
 * @since 2.0
 * @see com.rocket.summer.framework.context.i18n.LocaleContextHolder
 * @see com.rocket.summer.framework.web.context.request.RequestContextHolder
 * @see com.rocket.summer.framework.web.context.request.RequestContextListener
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 */
public class RequestContextFilter extends OncePerRequestFilter {

    private boolean threadContextInheritable = false;


    /**
     * Set whether to expose the LocaleContext and RequestAttributes as inheritable
     * for child threads (using an {@link java.lang.InheritableThreadLocal}).
     * <p>Default is "false", to avoid side effects on spawned background threads.
     * Switch this to "true" to enable inheritance for custom child threads which
     * are spawned during request processing and only used for this request
     * (that is, ending after their initial task, without reuse of the thread).
     * <p><b>WARNING:</b> Do not use inheritance for child threads if you are
     * accessing a thread pool which is configured to potentially add new threads
     * on demand (e.g. a JDK {@link java.util.concurrent.ThreadPoolExecutor}),
     * since this will expose the inherited context to such a pooled thread.
     */
    public void setThreadContextInheritable(boolean threadContextInheritable) {
        this.threadContextInheritable = threadContextInheritable;
    }


    /**
     * Returns "false" so that the filter may set up the request context in each
     * asynchronously dispatched thread.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /**
     * Returns "false" so that the filter may set up the request context in an
     * error dispatch.
     */
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        initContextHolders(request, attributes);

        try {
            filterChain.doFilter(request, response);
        }
        finally {
            resetContextHolders();
            if (logger.isDebugEnabled()) {
                logger.debug("Cleared thread-bound request context: " + request);
            }
            attributes.requestCompleted();
        }
    }

    private void initContextHolders(HttpServletRequest request, ServletRequestAttributes requestAttributes) {
        LocaleContextHolder.setLocale(request.getLocale(), this.threadContextInheritable);
        RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
        if (logger.isDebugEnabled()) {
            logger.debug("Bound request context to thread: " + request);
        }
    }

    private void resetContextHolders() {
        LocaleContextHolder.resetLocaleContext();
        RequestContextHolder.resetRequestAttributes();
    }

}

