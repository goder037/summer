package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.util.concurrent.Callable;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.async.WebAsyncUtils;
import com.rocket.summer.framework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

/**
 * Handles return values of type {@link Callable}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class CallableMethodReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Callable.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
        return (returnValue != null && returnValue instanceof Callable);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        Callable<?> callable = (Callable<?>) returnValue;
        WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(callable, mavContainer);
    }

}

