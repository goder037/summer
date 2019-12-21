package com.rocket.summer.framework.aop.scope;

import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.util.Assert;

/**
 * Default implementation of the {@link ScopedObject} interface.
 *
 * <p>Simply delegates the calls to the underlying
 * {@link ConfigurableBeanFactory bean factory}
 * ({@link ConfigurableBeanFactory#getBean(String)}/
 * {@link ConfigurableBeanFactory#destroyScopedBean(String)}).
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.beans.factory.BeanFactory#getBean
 * @see com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory#destroyScopedBean
 */
public class DefaultScopedObject implements ScopedObject {

    private final ConfigurableBeanFactory beanFactory;

    private final String targetBeanName;


    /**
     * Creates a new instance of the {@link DefaultScopedObject} class.
     * @param beanFactory the {@link ConfigurableBeanFactory} that holds the scoped target object
     * @param targetBeanName the name of the target bean
     * @throws IllegalArgumentException if either of the parameters is <code>null</code>; or
     * if the <code>targetBeanName</code> consists wholly of whitespace
     */
    public DefaultScopedObject(ConfigurableBeanFactory beanFactory, String targetBeanName) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.hasText(targetBeanName, "'targetBeanName' must not be empty");
        this.beanFactory = beanFactory;
        this.targetBeanName = targetBeanName;
    }


    public Object getTargetObject() {
        return this.beanFactory.getBean(this.targetBeanName);
    }

    public void removeFromScope() {
        this.beanFactory.destroyScopedBean(this.targetBeanName);
    }

}
