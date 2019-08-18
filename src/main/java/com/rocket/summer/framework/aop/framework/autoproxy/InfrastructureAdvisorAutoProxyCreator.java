package com.rocket.summer.framework.aop.framework.autoproxy;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Auto-proxy creator that considers infrastructure Advisor beans only,
 * ignoring any application-defined Advisors.
 *
 * @author Juergen Hoeller
 * @since 2.0.7
 */
@SuppressWarnings("serial")
public class InfrastructureAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator {

    private ConfigurableListableBeanFactory beanFactory;


    @Override
    protected void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.initBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    @Override
    protected boolean isEligibleAdvisorBean(String beanName) {
        return (this.beanFactory.containsBeanDefinition(beanName) &&
                this.beanFactory.getBeanDefinition(beanName).getRole() == BeanDefinition.ROLE_INFRASTRUCTURE);
    }

}

