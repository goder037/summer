package com.rocket.summer.framework.boot.context.embedded;

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
 * {@link BeanPostProcessor} that applies all {@link EmbeddedServletContainerCustomizer}s
 * from the bean factory to {@link ConfigurableEmbeddedServletContainer} beans.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class EmbeddedServletContainerCustomizerBeanPostProcessor
        implements BeanPostProcessor, BeanFactoryAware {

    private ListableBeanFactory beanFactory;

    private List<EmbeddedServletContainerCustomizer> customizers;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.isInstanceOf(ListableBeanFactory.class, beanFactory,
                "EmbeddedServletContainerCustomizerBeanPostProcessor can only be used "
                        + "with a ListableBeanFactory");
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof ConfigurableEmbeddedServletContainer) {
            postProcessBeforeInitialization((ConfigurableEmbeddedServletContainer) bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    private void postProcessBeforeInitialization(
            ConfigurableEmbeddedServletContainer bean) {
        for (EmbeddedServletContainerCustomizer customizer : getCustomizers()) {
            customizer.customize(bean);
        }
    }

    private Collection<EmbeddedServletContainerCustomizer> getCustomizers() {
        if (this.customizers == null) {
            // Look up does not include the parent context
            this.customizers = new ArrayList<EmbeddedServletContainerCustomizer>(
                    this.beanFactory
                            .getBeansOfType(EmbeddedServletContainerCustomizer.class,
                                    false, false)
                            .values());
            Collections.sort(this.customizers, AnnotationAwareOrderComparator.INSTANCE);
            this.customizers = Collections.unmodifiableList(this.customizers);
        }
        return this.customizers;
    }

}

