package com.rocket.summer.framework.web.context.request.async;

import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Assists with the invocation of {@link DeferredResultProcessingInterceptor}'s.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
class DeferredResultInterceptorChain {

    private static final Log logger = LogFactory.getLog(DeferredResultInterceptorChain.class);

    private final List<DeferredResultProcessingInterceptor> interceptors;

    private int preProcessingIndex = -1;


    public DeferredResultInterceptorChain(List<DeferredResultProcessingInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void applyBeforeConcurrentHandling(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.beforeConcurrentHandling(request, deferredResult);
        }
    }

    public void applyPreProcess(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.preProcess(request, deferredResult);
            this.preProcessingIndex++;
        }
    }

    public Object applyPostProcess(NativeWebRequest request,  DeferredResult<?> deferredResult, Object concurrentResult) {
        try {
            for (int i = this.preProcessingIndex; i >= 0; i--) {
                this.interceptors.get(i).postProcess(request, deferredResult, concurrentResult);
            }
        }
        catch (Throwable t) {
            return t;
        }
        return concurrentResult;
    }

    public void triggerAfterTimeout(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            if (deferredResult.isSetOrExpired()) {
                return;
            }
            if (!interceptor.handleTimeout(request, deferredResult)){
                break;
            }
        }
    }

    public void triggerAfterCompletion(NativeWebRequest request, DeferredResult<?> deferredResult) {
        for (int i = this.preProcessingIndex; i >= 0; i--) {
            try {
                this.interceptors.get(i).afterCompletion(request, deferredResult);
            }
            catch (Throwable t) {
                logger.error("afterCompletion error", t);
            }
        }
    }

}
