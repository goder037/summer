package com.rocket.summer.framework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.request.AsyncWebRequestInterceptor;
import com.rocket.summer.framework.web.context.request.WebRequestInterceptor;
import com.rocket.summer.framework.web.servlet.AsyncHandlerInterceptor;
import com.rocket.summer.framework.web.servlet.ModelAndView;

/**
 * Adapter that implements the Servlet HandlerInterceptor interface
 * and wraps an underlying WebRequestInterceptor.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.web.context.request.WebRequestInterceptor
 * @see com.rocket.summer.framework.web.servlet.HandlerInterceptor
 */
public class WebRequestHandlerInterceptorAdapter implements AsyncHandlerInterceptor {

    private final WebRequestInterceptor requestInterceptor;


    /**
     * Create a new WebRequestHandlerInterceptorAdapter for the given WebRequestInterceptor.
     * @param requestInterceptor the WebRequestInterceptor to wrap
     */
    public WebRequestHandlerInterceptorAdapter(WebRequestInterceptor requestInterceptor) {
        Assert.notNull(requestInterceptor, "WebRequestInterceptor must not be null");
        this.requestInterceptor = requestInterceptor;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        this.requestInterceptor.preHandle(new DispatcherServletWebRequest(request, response));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {

        this.requestInterceptor.postHandle(new DispatcherServletWebRequest(request, response),
                (modelAndView != null && !modelAndView.wasCleared() ? modelAndView.getModelMap() : null));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        this.requestInterceptor.afterCompletion(new DispatcherServletWebRequest(request, response), ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (this.requestInterceptor instanceof AsyncWebRequestInterceptor) {
            AsyncWebRequestInterceptor asyncInterceptor = (AsyncWebRequestInterceptor) this.requestInterceptor;
            DispatcherServletWebRequest webRequest = new DispatcherServletWebRequest(request, response);
            asyncInterceptor.afterConcurrentHandlingStarted(webRequest);
        }
    }

}
