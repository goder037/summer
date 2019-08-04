package com.rocket.summer.framework.aop.framework.autoproxy;

import com.rocket.summer.framework.aop.framework.AbstractAdvisingBeanPostProcessor;
import com.rocket.summer.framework.aop.framework.ProxyFactory;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Extension of {@link AbstractAutoProxyCreator} which implements {@link BeanFactoryAware},
 * adds exposure of the original target class for each proxied bean
 * ({@link AutoProxyUtils#ORIGINAL_TARGET_CLASS_ATTRIBUTE}),
 * and participates in an externally enforced target-class mode for any given bean
 * ({@link AutoProxyUtils#PRESERVE_TARGET_CLASS_ATTRIBUTE}).
 * This post-processor is therefore aligned with {@link AbstractAutoProxyCreator}.
 *
 * @author Juergen Hoeller
 * @since 4.2.3
 * @see AutoProxyUtils#shouldProxyTargetClass
 * @see AutoProxyUtils#determineTargetClass
 */
@SuppressWarnings("serial")
public abstract class AbstractBeanFactoryAwareAdvisingPostProcessor extends AbstractAdvisingBeanPostProcessor
        implements BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (beanFactory instanceof ConfigurableListableBeanFactory ?
                (ConfigurableListableBeanFactory) beanFactory : null);
    }

    @Override
    protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
        if (this.beanFactory != null) {
            AutoProxyUtils.exposeTargetClass(this.beanFactory, beanName, bean.getClass());
        }

        ProxyFactory proxyFactory = super.prepareProxyFactory(bean, beanName);
        if (!proxyFactory.isProxyTargetClass() && this.beanFactory != null &&
                AutoProxyUtils.shouldProxyTargetClass(this.beanFactory, beanName)) {
            proxyFactory.setProxyTargetClass(true);
        }
        return proxyFactory;
    }

}

