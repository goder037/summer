package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.format.FormatterRegistry;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.validation.MessageCodesResolver;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.servlet.HandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link WebMvcConfigurer} that delegates to one or more others.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
class WebMvcConfigurerComposite implements WebMvcConfigurer {

    private final List<WebMvcConfigurer> delegates = new ArrayList<WebMvcConfigurer>();


    public void addWebMvcConfigurers(List<WebMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.delegates.addAll(configurers);
        }
    }


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configurePathMatch(configurer);
        }
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureContentNegotiation(configurer);
        }
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureAsyncSupport(configurer);
        }
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureDefaultServletHandling(configurer);
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addFormatters(registry);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addInterceptors(registry);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addResourceHandlers(registry);
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addCorsMappings(registry);
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addViewControllers(registry);
        }
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureViewResolvers(registry);
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addArgumentResolvers(argumentResolvers);
        }
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.addReturnValueHandlers(returnValueHandlers);
        }
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureMessageConverters(converters);
        }
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.extendMessageConverters(converters);
        }
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureHandlerExceptionResolvers(exceptionResolvers);
        }
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.extendHandlerExceptionResolvers(exceptionResolvers);
        }
    }

    @Override
    public Validator getValidator() {
        Validator selected = null;
        for (WebMvcConfigurer configurer : this.delegates) {
            Validator validator = configurer.getValidator();
            if (validator != null) {
                if (selected != null) {
                    throw new IllegalStateException("No unique Validator found: {" +
                            selected + ", " + validator + "}");
                }
                selected = validator;
            }
        }
        return selected;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        MessageCodesResolver selected = null;
        for (WebMvcConfigurer configurer : this.delegates) {
            MessageCodesResolver messageCodesResolver = configurer.getMessageCodesResolver();
            if (messageCodesResolver != null) {
                if (selected != null) {
                    throw new IllegalStateException("No unique MessageCodesResolver found: {" +
                            selected + ", " + messageCodesResolver + "}");
                }
                selected = messageCodesResolver;
            }
        }
        return selected;
    }

}
