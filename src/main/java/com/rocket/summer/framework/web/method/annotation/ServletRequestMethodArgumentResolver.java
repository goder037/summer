package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.WebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.multipart.MultipartRequest;
import com.rocket.summer.framework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Locale;

/**
 * Resolves request-related method argument values of the following types:
 * <ul>
 * <li>{@link WebRequest}
 * <li>{@link ServletRequest}
 * <li>{@link MultipartRequest}
 * <li>{@link HttpSession}
 * <li>{@link Principal}
 * <li>{@link Locale}
 * <li>{@link InputStream}
 * <li>{@link Reader}
 * </ul>
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return WebRequest.class.isAssignableFrom(paramType) ||
                ServletRequest.class.isAssignableFrom(paramType) ||
                MultipartRequest.class.isAssignableFrom(paramType) ||
                HttpSession.class.isAssignableFrom(paramType) ||
                Principal.class.isAssignableFrom(paramType) ||
                Locale.class.equals(paramType) ||
                InputStream.class.isAssignableFrom(paramType) ||
                Reader.class.isAssignableFrom(paramType);
    }

    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws IOException {

        Class<?> paramType = parameter.getParameterType();
        if (WebRequest.class.isAssignableFrom(paramType)) {
            return webRequest;
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType)) {
            Object nativeRequest = webRequest.getNativeRequest(paramType);
            if (nativeRequest == null) {
                throw new IllegalStateException(
                        "Current request is not of type [" + paramType.getName() + "]: " + request);
            }
            return nativeRequest;
        }
        else if (HttpSession.class.isAssignableFrom(paramType)) {
            return request.getSession();
        }
        else if (Principal.class.isAssignableFrom(paramType)) {
            return request.getUserPrincipal();
        }
        else if (Locale.class.equals(paramType)) {
            return RequestContextUtils.getLocale(request);
        }
        else if (InputStream.class.isAssignableFrom(paramType)) {
            return request.getInputStream();
        }
        else if (Reader.class.isAssignableFrom(paramType)) {
            return request.getReader();
        }
        else {
            // should never happen..
            Method method = parameter.getMethod();
            throw new UnsupportedOperationException("Unknown parameter type: " + paramType + " in method: " + method);
        }
    }

}