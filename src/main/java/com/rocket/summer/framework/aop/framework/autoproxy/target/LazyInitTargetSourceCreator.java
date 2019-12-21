package com.rocket.summer.framework.aop.framework.autoproxy.target;

import com.rocket.summer.framework.aop.target.AbstractBeanFactoryBasedTargetSource;
import com.rocket.summer.framework.aop.target.LazyInitTargetSource;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * TargetSourceCreator that enforces a LazyInitTargetSource for each bean
 * that is defined as "lazy-init". This will lead to a proxy created for
 * each of those beans, allowing to fetch a reference to such a bean
 * without actually initializing the target bean instance.
 *
 * <p>To be registered as custom TargetSourceCreator for an auto-proxy creator,
 * in combination with custom interceptors for specific beans or for the
 * creation of lazy-init proxies only. For example, as autodetected
 * infrastructure bean in an XML application context definition:
 *
 * <pre class="code">
 * &lt;bean class="com.rocket.summer.framework.aop.framework.autoproxy.BeanNameAutoProxyCreator"&gt;
 *   &lt;property name="customTargetSourceCreators"&gt;
 *     &lt;list&gt;
 *       &lt;bean class="com.rocket.summer.framework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator"/&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="myLazyInitBean" class="mypackage.MyBeanClass" lazy-init="true"&gt;
 *   ...
 * &lt;/bean&gt;</pre>
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see com.rocket.summer.framework.beans.factory.config.BeanDefinition#isLazyInit
 * @see com.rocket.summer.framework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see com.rocket.summer.framework.aop.framework.autoproxy.BeanNameAutoProxyCreator
 */
public class LazyInitTargetSourceCreator extends AbstractBeanFactoryBasedTargetSourceCreator {

    @Override
    protected boolean isPrototypeBased() {
        return false;
    }

    @Override
    protected AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(
            Class<?> beanClass, String beanName) {

        if (getBeanFactory() instanceof ConfigurableListableBeanFactory) {
            BeanDefinition definition =
                    ((ConfigurableListableBeanFactory) getBeanFactory()).getBeanDefinition(beanName);
            if (definition.isLazyInit()) {
                return new LazyInitTargetSource();
            }
        }
        return null;
    }

}

