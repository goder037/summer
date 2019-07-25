package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.beans.factory.annotation.Autowired;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.format.FormatterRegistry;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.servlet.HandlerExceptionResolver;

import java.util.List;

/**
 * Extends {@link WebMvcConfigurationSupport} with the ability to detect beans
 * of type {@link WebMvcConfigurer} and give them a chance to customize the
 * provided configuration by delegating to them at the appropriate times.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 *
 * @see EnableWebMvc
 */
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {

    private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

    @Autowired(required = false)
    public void setConfigurers(List<WebMvcConfigurer> configurers) {
        if (configurers == null || configurers.isEmpty()) {
            return;
        }
        this.configurers.addWebMvcConfigurers(configurers);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        configurers.addInterceptors(registry);
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        configurers.addViewControllers(registry);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        configurers.addResourceHandlers(registry);
    }

    @Override
    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurers.configureDefaultServletHandling(configurer);
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        configurers.addArgumentResolvers(argumentResolvers);
    }

    @Override
    protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        configurers.addReturnValueHandlers(returnValueHandlers);
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        configurers.configureMessageConverters(converters);
    }

    @Override
    protected void addFormatters(FormatterRegistry registry) {
        configurers.addFormatters(registry);
    }

    @Override
    protected Validator getValidator() {
        return configurers.getValidator();
    }

    @Override
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        configurers.configureHandlerExceptionResolvers(exceptionResolvers);
    }

}
