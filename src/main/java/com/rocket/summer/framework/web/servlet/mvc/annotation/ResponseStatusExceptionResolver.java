package com.rocket.summer.framework.web.servlet.mvc.annotation;

import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.annotation.ResponseStatus;
import com.rocket.summer.framework.web.servlet.ModelAndView;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the {@link com.rocket.summer.framework.web.servlet.HandlerExceptionResolver HandlerExceptionResolver}
 * interface that uses the {@link ResponseStatus @ResponseStatus} annotation to map exceptions to HTTP status codes.
 *
 * <p>This exception resolver is enabled by default in the {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class ResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            try {
                return resolveResponseStatus(responseStatus, request, response, handler, ex);
            }
            catch (Exception resolveEx) {
                logger.warn("Handling of @ResponseStatus resulted in Exception", resolveEx);
            }
        }
        return null;
    }

    /**
     * Template method that handles {@link ResponseStatus @ResponseStatus} annotation. <p>Default implementation send a
     * response error using {@link HttpServletResponse#sendError(int)}, or {@link HttpServletResponse#sendError(int,
     * String)} if the annotation has a {@linkplain ResponseStatus#reason() reason}. Returns an empty ModelAndView.
     * @param responseStatus the annotation
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler, or <code>null</code> if none chosen at the time of the exception
     * (for example, if multipart resolution failed)
     * @param ex the exception that got thrown during handler execution
     * @return a corresponding ModelAndView to forward to, or <code>null</code> for default processing
     */
    protected ModelAndView resolveResponseStatus(ResponseStatus responseStatus, HttpServletRequest request,
                                                 HttpServletResponse response, Object handler, Exception ex) throws Exception {

        int statusCode = responseStatus.value().value();
        String reason = responseStatus.reason();
        if (!StringUtils.hasLength(reason)) {
            response.sendError(statusCode);
        }
        else {
            response.sendError(statusCode, reason);
        }
        return new ModelAndView();
    }

}

