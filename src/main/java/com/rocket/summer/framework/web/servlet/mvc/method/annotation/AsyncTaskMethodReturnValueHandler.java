package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.async.WebAsyncTask;
import com.rocket.summer.framework.web.context.request.async.WebAsyncUtils;
import com.rocket.summer.framework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

/**
 * Handles return values of type {@link WebAsyncTask}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class AsyncTaskMethodReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

    private final BeanFactory beanFactory;


    public AsyncTaskMethodReturnValueHandler(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return WebAsyncTask.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
        return (returnValue != null && returnValue instanceof WebAsyncTask);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        WebAsyncTask<?> webAsyncTask = (WebAsyncTask<?>) returnValue;
        webAsyncTask.setBeanFactory(this.beanFactory);
        WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(webAsyncTask, mavContainer);
    }

}

