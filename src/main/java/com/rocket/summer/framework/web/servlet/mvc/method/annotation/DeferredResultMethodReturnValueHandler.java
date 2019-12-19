package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.lang.UsesJava8;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.concurrent.ListenableFuture;
import com.rocket.summer.framework.util.concurrent.ListenableFutureCallback;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.async.DeferredResult;
import com.rocket.summer.framework.web.context.request.async.WebAsyncUtils;
import com.rocket.summer.framework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

/**
 * Handler for return values of type {@link DeferredResult}, {@link ListenableFuture},
 * {@link CompletionStage} and any other async type with a {@link #getAdapterMap()
 * registered adapter}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
@SuppressWarnings("deprecation")
public class DeferredResultMethodReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

    private final Map<Class<?>, DeferredResultAdapter> adapterMap;


    public DeferredResultMethodReturnValueHandler() {
        this.adapterMap = new HashMap<Class<?>, DeferredResultAdapter>(5);
        this.adapterMap.put(DeferredResult.class, new SimpleDeferredResultAdapter());
        this.adapterMap.put(ListenableFuture.class, new ListenableFutureAdapter());
        if (ClassUtils.isPresent("java.util.concurrent.CompletionStage", getClass().getClassLoader())) {
            this.adapterMap.put(CompletionStage.class, new CompletionStageAdapter());
        }
    }


    /**
     * Return the map with {@code DeferredResult} adapters.
     * <p>By default the map contains adapters for {@code DeferredResult}, which
     * simply downcasts, {@link ListenableFuture}, and {@link CompletionStage}.
     * @return the map of adapters
     * @deprecated in 4.3.8, see comments on {@link DeferredResultAdapter}
     */
    @Deprecated
    public Map<Class<?>, DeferredResultAdapter> getAdapterMap() {
        return this.adapterMap;
    }

    private DeferredResultAdapter getAdapterFor(Class<?> type) {
        for (Class<?> adapteeType : getAdapterMap().keySet()) {
            if (adapteeType.isAssignableFrom(type)) {
                return getAdapterMap().get(adapteeType);
            }
        }
        return null;
    }


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (getAdapterFor(returnType.getParameterType()) != null);
    }

    @Override
    public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
        return (returnValue != null && (getAdapterFor(returnValue.getClass()) != null));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        DeferredResultAdapter adapter = getAdapterFor(returnValue.getClass());
        if (adapter == null) {
            throw new IllegalStateException(
                    "Could not find DeferredResultAdapter for return value type: " + returnValue.getClass());
        }
        DeferredResult<?> result = adapter.adaptToDeferredResult(returnValue);
        WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(result, mavContainer);
    }


    /**
     * Adapter for {@code DeferredResult} return values.
     */
    private static class SimpleDeferredResultAdapter implements DeferredResultAdapter {

        @Override
        public DeferredResult<?> adaptToDeferredResult(Object returnValue) {
            Assert.isInstanceOf(DeferredResult.class, returnValue, "DeferredResult expected");
            return (DeferredResult<?>) returnValue;
        }
    }


    /**
     * Adapter for {@code ListenableFuture} return values.
     */
    private static class ListenableFutureAdapter implements DeferredResultAdapter {

        @Override
        public DeferredResult<?> adaptToDeferredResult(Object returnValue) {
            Assert.isInstanceOf(ListenableFuture.class, returnValue, "ListenableFuture expected");
            final DeferredResult<Object> result = new DeferredResult<Object>();
            ((ListenableFuture<?>) returnValue).addCallback(new ListenableFutureCallback<Object>() {
                @Override
                public void onSuccess(Object value) {
                    result.setResult(value);
                }
                @Override
                public void onFailure(Throwable ex) {
                    result.setErrorResult(ex);
                }
            });
            return result;
        }
    }


    /**
     * Adapter for {@code CompletionStage} return values.
     */
    @UsesJava8
    private static class CompletionStageAdapter implements DeferredResultAdapter {

        @Override
        public DeferredResult<?> adaptToDeferredResult(Object returnValue) {
            Assert.isInstanceOf(CompletionStage.class, returnValue, "CompletionStage expected");
            final DeferredResult<Object> result = new DeferredResult<Object>();
            @SuppressWarnings("unchecked")
            CompletionStage<?> future = (CompletionStage<?>) returnValue;
            future.handle(new BiFunction<Object, Throwable, Object>() {
                @Override
                public Object apply(Object value, Throwable ex) {
                    if (ex != null) {
                        result.setErrorResult(ex);
                    }
                    else {
                        result.setResult(value);
                    }
                    return null;
                }
            });
            return result;
        }
    }

}
