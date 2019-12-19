package com.rocket.summer.framework.web.servlet.mvc.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.context.MessageSource;
import com.rocket.summer.framework.context.MessageSourceAware;
import com.rocket.summer.framework.context.i18n.LocaleContextHolder;
import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.annotation.ResponseStatus;
import com.rocket.summer.framework.web.servlet.ModelAndView;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * A {@link com.rocket.summer.framework.web.servlet.HandlerExceptionResolver
 * HandlerExceptionResolver} that uses the {@link ResponseStatus @ResponseStatus}
 * annotation to map exceptions to HTTP status codes.
 *
 * <p>This exception resolver is enabled by default in the
 * {@link com.rocket.summer.framework.web.servlet.DispatcherServlet DispatcherServlet}
 * and the MVC Java config and the MVC namespace.
 *
 * <p>As of 4.2 this resolver also looks recursively for {@code @ResponseStatus}
 * present on cause exceptions, and as of 4.2.2 this resolver supports
 * attribute overrides for {@code @ResponseStatus} in custom composed annotations.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 3.0
 * @see ResponseStatus
 */
public class ResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver implements MessageSourceAware {

    private MessageSource messageSource;


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @Override
    protected ModelAndView doResolveException(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        ResponseStatus status = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
        if (status != null) {
            try {
                return resolveResponseStatus(status, request, response, handler, ex);
            }
            catch (Exception resolveEx) {
                logger.warn("ResponseStatus handling resulted in exception", resolveEx);
            }
        }
        else if (ex.getCause() instanceof Exception) {
            return doResolveException(request, response, handler, (Exception) ex.getCause());
        }
        return null;
    }

    /**
     * Template method that handles the {@link ResponseStatus @ResponseStatus} annotation.
     * <p>The default implementation sends a response error using
     * {@link HttpServletResponse#sendError(int)} or
     * {@link HttpServletResponse#sendError(int, String)} if the annotation has a
     * {@linkplain ResponseStatus#reason() reason} and then returns an empty ModelAndView.
     * @param responseStatus the annotation
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler, or {@code null} if none chosen at the
     * time of the exception, e.g. if multipart resolution failed
     * @param ex the exception
     * @return an empty ModelAndView, i.e. exception resolved
     */
    protected ModelAndView resolveResponseStatus(ResponseStatus responseStatus, HttpServletRequest request,
                                                 HttpServletResponse response, Object handler, Exception ex) throws Exception {

        int statusCode = responseStatus.code().value();
        String reason = responseStatus.reason();
        if (!StringUtils.hasLength(reason)) {
            response.sendError(statusCode);
        }
        else {
            String resolvedReason = (this.messageSource != null ?
                    this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale()) :
                    reason);
            response.sendError(statusCode, resolvedReason);
        }
        return new ModelAndView();
    }

}
