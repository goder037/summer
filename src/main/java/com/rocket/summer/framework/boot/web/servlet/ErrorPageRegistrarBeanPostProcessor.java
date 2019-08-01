package com.rocket.summer.framework.boot.web.servlet;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.ListableBeanFactory;
import com.rocket.summer.framework.beans.factory.config.BeanPostProcessor;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;
import com.rocket.summer.framework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link BeanPostProcessor} that applies all {@link ErrorPageRegistrar}s from the bean
 * factory to {@link ErrorPageRegistry} beans.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 1.4.0
 */
public class ErrorPageRegistrarBeanPostProcessor
        implements BeanPostProcessor, BeanFactoryAware {

    private ListableBeanFactory beanFactory;

    private List<ErrorPageRegistrar> registrars;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.isInstanceOf(ListableBeanFactory.class, beanFactory,
                "ErrorPageRegistrarBeanPostProcessor can only be used "
                        + "with a ListableBeanFactory");
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof ErrorPageRegistry) {
            postProcessBeforeInitialization((ErrorPageRegistry) bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    private void postProcessBeforeInitialization(ErrorPageRegistry registry) {
        for (ErrorPageRegistrar registrar : getRegistrars()) {
            registrar.registerErrorPages(registry);
        }
    }

    private Collection<ErrorPageRegistrar> getRegistrars() {
        if (this.registrars == null) {
            // Look up does not include the parent context
            this.registrars = new ArrayList<ErrorPageRegistrar>(this.beanFactory
                    .getBeansOfType(ErrorPageRegistrar.class, false, false).values());
            Collections.sort(this.registrars, AnnotationAwareOrderComparator.INSTANCE);
            this.registrars = Collections.unmodifiableList(this.registrars);
        }
        return this.registrars;
    }

}

