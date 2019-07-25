package com.rocket.summer.framework.web.servlet.handler;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Interceptor that places the configured {@link ConversionService} in request scope
 * so it's available during request processing. The request attribute name is
 * "org.springframework.core.convert.ConversionService", the value of
 * <code>ConversionService.class.getName()</code>.
 *
 * <p>Mainly for use within JSP tags such as the spring:eval tag.
 *
 * @author Keith Donald
 * @since 3.0.1
 */
public class ConversionServiceExposingInterceptor extends HandlerInterceptorAdapter {

    private final ConversionService conversionService;


    /**
     * Creates a new {@link ConversionServiceExposingInterceptor}.
     * @param conversionService the conversion service to export to request scope when this interceptor is invoked
     */
    public ConversionServiceExposingInterceptor(ConversionService conversionService) {
        Assert.notNull(conversionService, "The ConversionService may not be null");
        this.conversionService = conversionService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException, IOException {

        request.setAttribute(ConversionService.class.getName(), this.conversionService);
        return true;
    }

}

