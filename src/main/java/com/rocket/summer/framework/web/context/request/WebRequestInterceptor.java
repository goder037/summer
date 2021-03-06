package com.rocket.summer.framework.web.context.request;

import com.rocket.summer.framework.web.ui.ModelMap;

/**
 * Interface for general web request interception. Allows for being applied
 * to Servlet request as well as Portlet request environments, by building
 * on the {@link WebRequest} abstraction.
 *
 * <p>This interface assumes MVC-style request processing: A handler gets executed,
 * exposes a set of model objects, then a view gets rendered based on that model.
 * Alternatively, a handler may also process the request completely, with no
 * view to be rendered.
 *
 * <p>This interface is deliberately minimalistic to keep the dependencies of
 * generic request interceptors as minimal as feasible.
 *
 * <p><b>NOTE:</b> While this interceptor is applied to the entire request processing
 * in a Servlet environment, it is by default only applied to the <i>render</i> phase
 * in a Portlet environment, preparing and rendering a Portlet view. To apply
 * WebRequestInterceptors to the <i>action</i> phase as well, set the HandlerMapping's
 * "applyWebRequestInterceptorsToRenderPhaseOnly" flag to "false". Alternatively,
 * consider using the Portlet-specific HandlerInterceptor mechanism for such needs.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ServletWebRequest
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 * @see com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see com.rocket.summer.framework.web.servlet.HandlerInterceptor
 */
public interface WebRequestInterceptor {

    /**
     * Intercept the execution of a request handler <i>before</i> its invocation.
     * <p>Allows for preparing context resources (such as a Hibernate Session)
     * and expose them as request attributes or as thread-local objects.
     * @param request the current web request
     * @throws Exception in case of errors
     */
    void preHandle(WebRequest request) throws Exception;

    /**
     * Intercept the execution of a request handler <i>after</i> its successful
     * invocation, right before view rendering (if any).
     * <p>Allows for modifying context resources after successful handler
     * execution (for example, flushing a Hibernate Session).
     * @param request the current web request
     * @param model the map of model objects that will be exposed to the view
     * (may be <code>null</code>). Can be used to analyze the exposed model
     * and/or to add further model attributes, if desired.
     * @throws Exception in case of errors
     */
    void postHandle(WebRequest request, ModelMap model) throws Exception;

    /**
     * Callback after completion of request processing, that is, after rendering
     * the view. Will be called on any outcome of handler execution, thus allows
     * for proper resource cleanup.
     * <p>Note: Will only be called if this interceptor's <code>preHandle</code>
     * method has successfully completed!
     * @param request the current web request
     * @param ex exception thrown on handler execution, if any
     * @throws Exception in case of errors
     */
    void afterCompletion(WebRequest request, Exception ex) throws Exception;

}
