package com.rocket.summer.framework.aop.framework.autoproxy;

import com.rocket.summer.framework.aop.TargetSource;

/**
 * Implementations can create special target sources, such as pooling target
 * sources, for particular beans. For example, they may base their choice
 * on attributes, such as a pooling attribute, on the target class.
 *
 * <p>AbstractAutoProxyCreator can support a number of TargetSourceCreators,
 * which will be applied in order.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface TargetSourceCreator {

    /**
     * Create a special TargetSource for the given bean, if any.
     * @param beanClass the class of the bean to create a TargetSource for
     * @param beanName the name of the bean
     * @return a special TargetSource or {@code null} if this TargetSourceCreator isn't
     * interested in the particular bean
     */
    TargetSource getTargetSource(Class<?> beanClass, String beanName);

}

