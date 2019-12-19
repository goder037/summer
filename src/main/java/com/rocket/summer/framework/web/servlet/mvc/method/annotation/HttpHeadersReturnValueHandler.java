package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.server.ServletServerHttpResponse;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

/**
 * Handles {@link HttpHeaders} return values.
 *
 * @author Stephane Nicoll
 * @since 4.0.1
 */
public class HttpHeadersReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return HttpHeaders.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    @SuppressWarnings("resource")
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        mavContainer.setRequestHandled(true);

        Assert.state(returnValue instanceof HttpHeaders, "HttpHeaders expected");
        HttpHeaders headers = (HttpHeaders) returnValue;

        if (!headers.isEmpty()) {
            HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
            ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
            outputMessage.getHeaders().putAll(headers);
            outputMessage.getBody();  // flush headers
        }
    }

}
