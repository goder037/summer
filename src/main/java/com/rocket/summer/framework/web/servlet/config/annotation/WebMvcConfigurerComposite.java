package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.format.FormatterRegistry;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.servlet.HandlerExceptionResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link WebMvcConfigurer} implementation that delegates to other {@link WebMvcConfigurer} instances.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
class WebMvcConfigurerComposite implements WebMvcConfigurer {

    private final List<WebMvcConfigurer> delegates = new ArrayList<WebMvcConfigurer>();

    public void addWebMvcConfigurers(List<WebMvcConfigurer> configurers) {
        if (configurers != null) {
            this.delegates.addAll(configurers);
        }
    }

    public void addFormatters(FormatterRegistry registry) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.addFormatters(registry);
        }
    }

    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.configureMessageConverters(converters);
        }
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.addArgumentResolvers(argumentResolvers);
        }
    }

    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.addReturnValueHandlers(returnValueHandlers);
        }
    }

    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.configureHandlerExceptionResolvers(exceptionResolvers);
        }
    }

    public void addInterceptors(InterceptorRegistry registry) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.addInterceptors(registry);
        }
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.addViewControllers(registry);
        }
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.addResourceHandlers(registry);
        }
    }

    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        for (WebMvcConfigurer delegate : delegates) {
            delegate.configureDefaultServletHandling(configurer);
        }
    }

    public Validator getValidator() {
        Map<WebMvcConfigurer, Validator> validators = new HashMap<WebMvcConfigurer, Validator>();
        for (WebMvcConfigurer delegate : delegates) {
            Validator validator = delegate.getValidator();
            if (validator != null) {
                validators.put(delegate, validator);
            }
        }
        if (validators.size() == 0) {
            return null;
        }
        else if (validators.size() == 1) {
            return validators.values().iterator().next();
        }
        else {
            throw new IllegalStateException(
                    "Multiple custom validators provided from [" + validators.keySet() + "]");
        }
    }

}

