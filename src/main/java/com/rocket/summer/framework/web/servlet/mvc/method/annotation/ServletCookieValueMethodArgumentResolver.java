package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.annotation.AbstractCookieValueMethodArgumentResolver;
import com.rocket.summer.framework.web.util.UrlPathHelper;
import com.rocket.summer.framework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * An {@link com.rocket.summer.framework.web.method.annotation.AbstractCookieValueMethodArgumentResolver} that resolves cookie
 * values from an {@link HttpServletRequest}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletCookieValueMethodArgumentResolver extends AbstractCookieValueMethodArgumentResolver {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public ServletCookieValueMethodArgumentResolver(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Override
    protected Object resolveName(String cookieName, MethodParameter parameter, NativeWebRequest webRequest)
            throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Cookie cookieValue = WebUtils.getCookie(servletRequest, cookieName);
        if (Cookie.class.isAssignableFrom(parameter.getParameterType())) {
            return cookieValue;
        }
        else if (cookieValue != null) {
            return this.urlPathHelper.decodeRequestString(servletRequest, cookieValue.getValue());
        }
        else {
            return null;
        }
    }
}