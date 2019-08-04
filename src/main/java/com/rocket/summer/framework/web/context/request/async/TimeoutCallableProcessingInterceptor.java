package com.rocket.summer.framework.web.context.request.async;

import com.rocket.summer.framework.web.context.request.NativeWebRequest;

import java.util.concurrent.Callable;

/**
 * Sends a 503 (SERVICE_UNAVAILABLE) in case of a timeout if the response is not
 * already committed. As of 4.2.8 this is done indirectly by setting the result
 * to an {@link AsyncRequestTimeoutException} which is then handled by
 * Spring MVC's default exception handling as a 503 error.
 *
 * <p>Registered at the end, after all other interceptors and
 * therefore invoked only if no other interceptor handles the timeout.
 *
 * <p>Note that according to RFC 7231, a 503 without a 'Retry-After' header is
 * interpreted as a 500 error and the client should not retry. Applications
 * can install their own interceptor to handle a timeout and add a 'Retry-After'
 * header if necessary.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class TimeoutCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter {

    @Override
    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        return new AsyncRequestTimeoutException();
    }

}
