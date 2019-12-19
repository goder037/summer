package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;

/**
 * Resolves response-related method argument values of types:
 * <ul>
 * <li>{@link ServletResponse}
 * <li>{@link OutputStream}
 * <li>{@link Writer}
 * </ul>
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return ServletResponse.class.isAssignableFrom(paramType)
                || OutputStream.class.isAssignableFrom(paramType)
                || Writer.class.isAssignableFrom(paramType);
    }

    /**
     * Set {@link ModelAndViewContainer#setRequestHandled(boolean)} to
     * {@code false} to indicate that the method signature provides access
     * to the response. If subsequently the underlying method returns
     * {@code null}, the request is considered directly handled.
     */
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws IOException {

        if (mavContainer != null) {
            mavContainer.setRequestHandled(true);
        }

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Class<?> paramType = parameter.getParameterType();

        if (ServletResponse.class.isAssignableFrom(paramType)) {
            Object nativeResponse = webRequest.getNativeResponse(paramType);
            if (nativeResponse == null) {
                throw new IllegalStateException(
                        "Current response is not of type [" + paramType.getName() + "]: " + response);
            }
            return nativeResponse;
        }
        else if (OutputStream.class.isAssignableFrom(paramType)) {
            return response.getOutputStream();
        }
        else if (Writer.class.isAssignableFrom(paramType)) {
            return response.getWriter();
        }
        else {
            // should not happen
            Method method = parameter.getMethod();
            throw new UnsupportedOperationException("Unknown parameter type: " + paramType + " in method: " + method);
        }
    }

}
