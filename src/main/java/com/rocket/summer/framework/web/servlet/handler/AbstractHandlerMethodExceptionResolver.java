package com.rocket.summer.framework.web.servlet.handler;

import com.rocket.summer.framework.web.method.HandlerMethod;
import com.rocket.summer.framework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract base class for
 * {@link org.springframework.web.servlet.HandlerExceptionResolver HandlerExceptionResolver}
 * implementations that support handling exceptions from handlers of type {@link HandlerMethod}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractHandlerMethodExceptionResolver extends AbstractHandlerExceptionResolver {

    /**
     * Checks if the handler is a {@link HandlerMethod} instance and performs the check against the bean
     * instance it contains. If the provided handler is not an instance of {@link HandlerMethod},
     * {@code false} is returned instead.
     */
    @Override
    protected boolean shouldApplyTo(HttpServletRequest request, Object handler) {
        if (handler == null) {
            return super.shouldApplyTo(request, handler);
        }
        else if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            handler = handlerMethod.getBean();
            return super.shouldApplyTo(request, handler);
        }
        else {
            return false;
        }
    }

    @Override
    protected final ModelAndView doResolveException(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {

        return doResolveHandlerMethodException(request, response, (HandlerMethod) handler, ex);
    }

    /**
     * Actually resolve the given exception that got thrown during on handler execution,
     * returning a ModelAndView that represents a specific error page if appropriate.
     * <p>May be overridden in subclasses, in order to apply specific exception checks.
     * Note that this template method will be invoked <i>after</i> checking whether this
     * resolved applies ("mappedHandlers" etc), so an implementation may simply proceed
     * with its actual exception handling.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handlerMethod the executed handler method, or <code>null</code> if none chosen at the time
     * of the exception (for example, if multipart resolution failed)
     * @param ex the exception that got thrown during handler execution
     * @return a corresponding ModelAndView to forward to, or <code>null</code> for default processing
     */
    protected abstract ModelAndView doResolveHandlerMethodException(
            HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, Exception ex);

}

