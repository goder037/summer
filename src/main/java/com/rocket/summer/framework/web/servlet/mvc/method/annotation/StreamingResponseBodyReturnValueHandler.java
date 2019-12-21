package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.io.OutputStream;
import java.util.concurrent.Callable;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.http.ResponseEntity;
import com.rocket.summer.framework.http.server.ServerHttpResponse;
import com.rocket.summer.framework.http.server.ServletServerHttpResponse;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.async.WebAsyncUtils;
import com.rocket.summer.framework.web.filter.ShallowEtagHeaderFilter;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

/**
 * Supports return values of type
 * {@link com.rocket.summer.framework.web.servlet.mvc.method.annotation.StreamingResponseBody}
 * and also {@code ResponseEntity<StreamingResponseBody>}.
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 */
public class StreamingResponseBodyReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        if (StreamingResponseBody.class.isAssignableFrom(returnType.getParameterType())) {
            return true;
        }
        else if (ResponseEntity.class.isAssignableFrom(returnType.getParameterType())) {
            Class<?> bodyType = ResolvableType.forMethodParameter(returnType).getGeneric(0).resolve();
            return (bodyType != null && StreamingResponseBody.class.isAssignableFrom(bodyType));
        }
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);

        if (returnValue instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) returnValue;
            response.setStatus(responseEntity.getStatusCodeValue());
            outputMessage.getHeaders().putAll(responseEntity.getHeaders());
            returnValue = responseEntity.getBody();
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                outputMessage.flush();
                return;
            }
        }

        ServletRequest request = webRequest.getNativeRequest(ServletRequest.class);
        ShallowEtagHeaderFilter.disableContentCaching(request);

        Assert.isInstanceOf(StreamingResponseBody.class, returnValue, "StreamingResponseBody expected");
        StreamingResponseBody streamingBody = (StreamingResponseBody) returnValue;

        Callable<Void> callable = new StreamingResponseBodyTask(outputMessage.getBody(), streamingBody);
        WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(callable, mavContainer);
    }


    private static class StreamingResponseBodyTask implements Callable<Void> {

        private final OutputStream outputStream;

        private final StreamingResponseBody streamingBody;

        public StreamingResponseBodyTask(OutputStream outputStream, StreamingResponseBody streamingBody) {
            this.outputStream = outputStream;
            this.streamingBody = streamingBody;
        }

        @Override
        public Void call() throws Exception {
            this.streamingBody.writeTo(this.outputStream);
            return null;
        }
    }

}

