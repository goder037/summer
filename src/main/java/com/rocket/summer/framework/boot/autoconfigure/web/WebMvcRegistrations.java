package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Interface to register key components of the {@link WebMvcConfigurationSupport} in place
 * of the default ones provided by Spring MVC.
 * <p>
 * All custom instances are later processed by Boot and Spring MVC configurations. A
 * single instance of this component should be registered, otherwise making it impossible
 * to choose from redundant MVC components.
 *
 * @author Brian Clozel
 * @since 1.4.0
 * @see com.rocket.summer.framework.boot.autoconfigure.web.WebMvcAutoConfiguration.EnableWebMvcConfiguration
 */
public interface WebMvcRegistrations {

    /**
     * Return the custom {@link RequestMappingHandlerMapping} that should be used and
     * processed by the MVC configuration.
     * @return the custom {@link RequestMappingHandlerMapping} instance
     */
    RequestMappingHandlerMapping getRequestMappingHandlerMapping();

    /**
     * Return the custom {@link RequestMappingHandlerAdapter} that should be used and
     * processed by the MVC configuration.
     * @return the custom {@link RequestMappingHandlerAdapter} instance
     */
    RequestMappingHandlerAdapter getRequestMappingHandlerAdapter();

    /**
     * Return the custom {@link ExceptionHandlerExceptionResolver} that should be used and
     * processed by the MVC configuration.
     * @return the custom {@link ExceptionHandlerExceptionResolver} instance
     */
    ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver();

}
