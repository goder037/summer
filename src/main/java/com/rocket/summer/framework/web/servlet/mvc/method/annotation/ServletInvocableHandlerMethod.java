package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.http.HttpStatus;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.annotation.ResponseStatus;
import com.rocket.summer.framework.web.context.request.ServletWebRequest;
import com.rocket.summer.framework.web.method.HandlerMethod;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import com.rocket.summer.framework.web.method.support.InvocableHandlerMethod;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.View;
import com.rocket.summer.framework.web.util.NestedServletException;

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
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {

    private static final Method CALLABLE_METHOD = ClassUtils.getMethod(Callable.class, "call");

    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;


    /**
     * Creates an instance from the given handler and method.
     */
    public ServletInvocableHandlerMethod(Object handler, Method method) {
        super(handler, method);
    }

    /**
     * Create an instance from a {@code HandlerMethod}.
     */
    public ServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }


    /**
     * Register {@link HandlerMethodReturnValueHandler} instances to use to
     * handle return values.
     */
    public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }


    /**
     * Invoke the method and handle the return value through one of the
     * configured {@link HandlerMethodReturnValueHandler}s.
     * @param webRequest the current request
     * @param mavContainer the ModelAndViewContainer for this request
     * @param providedArgs "given" arguments matched by type (not resolved)
     */
    public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
                                Object... providedArgs) throws Exception {

        Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
        setResponseStatus(webRequest);

        if (returnValue == null) {
            if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
                mavContainer.setRequestHandled(true);
                return;
            }
        }
        else if (StringUtils.hasText(getResponseStatusReason())) {
            mavContainer.setRequestHandled(true);
            return;
        }

        mavContainer.setRequestHandled(false);
        try {
            this.returnValueHandlers.handleReturnValue(
                    returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
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
        HttpStatus status = getResponseStatus();
        if (status == null) {
            return;
        }

        String reason = getResponseStatusReason();
        if (StringUtils.hasText(reason)) {
            webRequest.getResponse().sendError(status.value(), reason);
        }
        else {
            webRequest.getResponse().setStatus(status.value());
        }

        // To be picked up by RedirectView
        webRequest.getRequest().setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, status);
    }

    /**
     * Does the given request qualify as "not modified"?
     * @see ServletWebRequest#checkNotModified(long)
     * @see ServletWebRequest#checkNotModified(String)
     */
    private boolean isRequestNotModified(ServletWebRequest webRequest) {
        return webRequest.isNotModified();
    }

    private String getReturnValueHandlingErrorMessage(String message, Object returnValue) {
        StringBuilder sb = new StringBuilder(message);
        if (returnValue != null) {
            sb.append(" [type=").append(returnValue.getClass().getName()).append("]");
        }
        sb.append(" [value=").append(returnValue).append("]");
        return getDetailedErrorMessage(sb.toString());
    }

    /**
     * Create a nested ServletInvocableHandlerMethod subclass that returns the
     * the given value (or raises an Exception if the value is one) rather than
     * actually invoking the controller method. This is useful when processing
     * async return values (e.g. Callable, DeferredResult, ListenableFuture).
     */
    ServletInvocableHandlerMethod wrapConcurrentResult(Object result) {
        return new ConcurrentResultHandlerMethod(result, new ConcurrentResultMethodParameter(result));
    }


    /**
     * A nested subclass of {@code ServletInvocableHandlerMethod} that uses a
     * simple {@link Callable} instead of the original controller as the handler in
     * order to return the fixed (concurrent) result value given to it. Effectively
     * "resumes" processing with the asynchronously produced return value.
     */
    private class ConcurrentResultHandlerMethod extends ServletInvocableHandlerMethod {

        private final MethodParameter returnType;

        public ConcurrentResultHandlerMethod(final Object result, ConcurrentResultMethodParameter returnType) {
            super(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    if (result instanceof Exception) {
                        throw (Exception) result;
                    }
                    else if (result instanceof Throwable) {
                        throw new NestedServletException("Async processing failed", (Throwable) result);
                    }
                    return result;
                }
            }, CALLABLE_METHOD);

            setHandlerMethodReturnValueHandlers(ServletInvocableHandlerMethod.this.returnValueHandlers);
            this.returnType = returnType;
        }

        /**
         * Bridge to actual controller type-level annotations.
         */
        @Override
        public Class<?> getBeanType() {
            return ServletInvocableHandlerMethod.this.getBeanType();
        }

        /**
         * Bridge to actual return value or generic type within the declared
         * async return type, e.g. Foo instead of {@code DeferredResult<Foo>}.
         */
        @Override
        public MethodParameter getReturnValueType(Object returnValue) {
            return this.returnType;
        }

        /**
         * Bridge to controller method-level annotations.
         */
        @Override
        public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
            return ServletInvocableHandlerMethod.this.getMethodAnnotation(annotationType);
        }

        /**
         * Bridge to controller method-level annotations.
         */
        @Override
        public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
            return ServletInvocableHandlerMethod.this.hasMethodAnnotation(annotationType);
        }
    }


    /**
     * MethodParameter subclass based on the actual return value type or if
     * that's null falling back on the generic type within the declared async
     * return type, e.g. Foo instead of {@code DeferredResult<Foo>}.
     */
    private class ConcurrentResultMethodParameter extends HandlerMethodParameter {

        private final Object returnValue;

        private final ResolvableType returnType;

        public ConcurrentResultMethodParameter(Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
            this.returnType = ResolvableType.forType(super.getGenericParameterType()).getGeneric(0);
        }

        public ConcurrentResultMethodParameter(ConcurrentResultMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
            this.returnType = original.returnType;
        }

        @Override
        public Class<?> getParameterType() {
            if (this.returnValue != null) {
                return this.returnValue.getClass();
            }
            if (!ResolvableType.NONE.equals(this.returnType)) {
                return this.returnType.resolve(Object.class);
            }
            return super.getParameterType();
        }

        @Override
        public Type getGenericParameterType() {
            return this.returnType.getType();
        }

        @Override
        public ConcurrentResultMethodParameter clone() {
            return new ConcurrentResultMethodParameter(this);
        }
    }

}
