package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.beans.factory.config.BeanPostProcessor;
import com.rocket.summer.framework.context.*;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}
 * implementation that passes the ApplicationContext to beans that
 * implement the {@link ResourceLoaderAware}, {@link MessageSourceAware},
 * {@link ApplicationEventPublisherAware} and/or
 * {@link ApplicationContextAware} interfaces.
 * If all of them are implemented, they are satisfied in the given order.
 *
 * <p>Application contexts will automatically register this with their
 * underlying bean factory. Applications do not use this directly.
 *
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see org.springframework.context.ResourceLoaderAware
 * @see org.springframework.context.MessageSourceAware
 * @see org.springframework.context.ApplicationEventPublisherAware
 * @see org.springframework.context.ApplicationContextAware
 * @see org.springframework.context.support.AbstractApplicationContext#refresh()
 */
class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;


    /**
     * Create a new ApplicationContextAwareProcessor for the given context.
     */
    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ResourceLoaderAware) {
            ((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
        }
        if (bean instanceof ApplicationEventPublisherAware) {
            ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
        }
        if (bean instanceof MessageSourceAware) {
            ((MessageSourceAware) bean).setMessageSource(this.applicationContext);
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String name) {
        return bean;
    }

}

