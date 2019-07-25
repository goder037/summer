package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.http.HttpStatus;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.annotation.ResponseStatus;
import com.rocket.summer.framework.web.context.request.ServletWebRequest;
import com.rocket.summer.framework.web.method.HandlerMethod;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import com.rocket.summer.framework.web.method.support.InvocableHandlerMethod;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.View;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Extends {@link InvocableHandlerMethod} with the ability to handle return
 * values through a registered {@link HandlerMethodReturnValueHandler} and
 * also supports setting the response status based on a method-level
 * {@code @ResponseStatus} annotation.
 *
 * <p>A {@code null} return value (including void) may be interpreted as the
 * end of request processing in combination with a {@code @ResponseStatus}
 * annotation, a not-modified check condition
 * (see {@link ServletWebRequest#checkNotModified(long)}), or
 * a method argument that provides access to the response stream.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {

    private HttpStatus responseStatus;

    private String responseReason;

    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;


    /**
     * Creates an instance from the given handler and method.
     */
    public ServletInvocableHandlerMethod(Object handler, Method method) {
        super(handler, method);
        initResponseStatus();
    }

    /**
     * Create an instance from a {@code HandlerMethod}.
     */
    public ServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
        initResponseStatus();
    }

    private void initResponseStatus() {
        ResponseStatus annot = getMethodAnnotation(ResponseStatus.class);
        if (annot != null) {
            this.responseStatus = annot.value();
            this.responseReason = annot.reason();
        }
    }

    /**
     * Register {@link HandlerMethodReturnValueHandler} instances to use to
     * handle return values.
     */
    public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    /**
     * Invokes the method and handles the return value through a registered
     * {@link HandlerMethodReturnValueHandler}.
     *
     * @param webRequest the current request
     * @param mavContainer the ModelAndViewContainer for this request
     * @param providedArgs "given" arguments matched by type, not resolved
     */
    public final void invokeAndHandle(ServletWebRequest webRequest,
                                      ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {

        Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);

        setResponseStatus(webRequest);

        if (returnValue == null) {
            if (isRequestNotModified(webRequest) || hasResponseStatus() || mavContainer.isRequestHandled()) {
                mavContainer.setRequestHandled(true);
                return;
            }
        }
        else if (StringUtils.hasText(this.responseReason)) {
            mavContainer.setRequestHandled(true);
            return;
        }

        mavContainer.setRequestHandled(false);

        try {
            this.returnValueHandlers.handleReturnValue(returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
        }
        catch (Exception ex) {
            if (logger.isTraceEnabled()) {
                logger.trace(getReturnValueHandlingErrorMessage("Error handling return value", returnValue), ex);
            }
            throw ex;
        }
    }

    /**
     * Set the response status according to the {@link ResponseStatus} annotation.
     */
    private void setResponseStatus(ServletWebRequest webRequest) throws IOException {
        if (this.responseStatus == null) {
            return;
        }

        if (StringUtils.hasText(this.responseReason)) {
            webRequest.getResponse().sendError(this.responseStatus.value(), this.responseReason);
        }
        else {
            webRequest.getResponse().setStatus(this.responseStatus.value());
        }

        // to be picked up by the RedirectView
        webRequest.getRequest().setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, this.responseStatus);
    }

    /**
     * Does the given request qualify as "not modified"?
     * @see ServletWebRequest#checkNotModified(long)
     * @see ServletWebRequest#checkNotModified(String)
     */
    private boolean isRequestNotModified(ServletWebRequest webRequest) {
        return webRequest.isNotModified();
    }

    /**
     * Does this method have the response status instruction?
     */
    private boolean hasResponseStatus() {
        return responseStatus != null;
    }

    private String getReturnValueHandlingErrorMessage(String message, Object returnValue) {
        StringBuilder sb = new StringBuilder(message);
        if (returnValue != null) {
            sb.append(" [type=" + returnValue.getClass().getName() + "] ");
        }
        sb.append("[value=" + returnValue + "]");
        return getDetailedErrorMessage(sb.toString());
    }

}
