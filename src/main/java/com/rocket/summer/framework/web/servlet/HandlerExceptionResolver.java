package com.rocket.summer.framework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface to be implemented by objects than can resolve exceptions thrown
 * during handler mapping or execution, in the typical case to error views.
 * Implementors are typically registered as beans in the application context.
 *
 * <p>Error views are analogous to the error page JSPs, but can be used with
 * any kind of exception including any checked exception, with potentially
 * fine-granular mappings for specific handlers.
 *
 * @author Juergen Hoeller
 * @since 22.11.2003
 */
public interface HandlerExceptionResolver {

    /**
     * Try to resolve the given exception that got thrown during on handler execution,
     * returning a ModelAndView that represents a specific error page if appropriate.
     * <p>The returned ModelAndView may be {@linkplain ModelAndView#isEmpty() empty}
     * to indicate that the exception has been resolved successfully but that no view
     * should be rendered, for instance by setting a status code.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler, or <code>null</code> if none chosen at the
     * time of the exception (for example, if multipart resolution failed)
     * @param ex the exception that got thrown during handler execution
     * @return a corresponding ModelAndView to forward to,
     * or <code>null</code> for default processing
     */
    ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);

}

