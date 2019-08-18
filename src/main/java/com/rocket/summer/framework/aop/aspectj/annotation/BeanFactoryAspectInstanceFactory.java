package com.rocket.summer.framework.aop.aspectj.annotation;

import java.io.Serializable;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.OrderUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * {@link com.rocket.summer.framework.aop.aspectj.AspectInstanceFactory} implementation
 * backed by a Spring {@link com.rocket.summer.framework.beans.factory.BeanFactory}.
 *
 * <p>Note that this may instantiate multiple times if using a prototype,
 * which probably won't give the semantics you expect.
 * Use a {@link LazySingletonAspectInstanceFactoryDecorator}
 * to wrap this to ensure only one new aspect comes back.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.beans.factory.BeanFactory
 * @see LazySingletonAspectInstanceFactoryDecorator
 */
@SuppressWarnings("serial")
public class BeanFactoryAspectInstanceFactory implements MetadataAwareAspectInstanceFactory, Serializable {

    private final BeanFactory beanFactory;

    private final String name;

    private final AspectMetadata aspectMetadata;


    /**
     * Create a BeanFactoryAspectInstanceFactory. AspectJ will be called to
     * introspect to create AJType metadata using the type returned for the
     * given bean name from the BeanFactory.
     * @param beanFactory BeanFactory to obtain instance(s) from
     * @param name name of the bean
     */
    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name) {
        this(beanFactory, name, beanFactory.getType(name));
    }

    /**
     * Create a BeanFactoryAspectInstanceFactory, providing a type that AspectJ should
     * introspect to create AJType metadata. Use if the BeanFactory may consider the type
     * to be a subclass (as when using CGLIB), and the information should relate to a superclass.
     * @param beanFactory BeanFactory to obtain instance(s) from
     * @param name the name of the bean
     * @param type the type that should be introspected by AspectJ
     */
    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name, Class<?> type) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.notNull(name, "Bean name must not be null");
        this.beanFactory = beanFactory;
        this.name = name;
        this.aspectMetadata = new AspectMetadata(type, name);
    }


    @Override
    public Object getAspectInstance() {
        return this.beanFactory.getBean(this.name);
    }

    @Override
    public ClassLoader getAspectClassLoader() {
        return (this.beanFactory instanceof ConfigurableBeanFactory ?
                ((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader() :
                ClassUtils.getDefaultClassLoader());
    }

    @Override
    public AspectMetadata getAspectMetadata() {
        return this.aspectMetadata;
    }

    @Override
    public Object getAspectCreationMutex() {
        if (this.beanFactory != null) {
            if (this.beanFactory.isSingleton(name)) {
                // Rely on singleton semantics provided by the factory -> no local lock.
                return null;
            }
            else if (this.beanFactory instanceof ConfigurableBeanFactory) {
                // No singleton guarantees from the factory -> let's lock locally but
                // reuse the factory's singleton lock, just in case a lazy dependency
                // of our advice bean happens to trigger the singleton lock implicitly...
                return ((ConfigurableBeanFactory) this.beanFactory).getSingletonMutex();
            }
        }
        return this;
    }

    /**
     * Determine the order for this factory's target aspect, either
     * an instance-specific order expressed through implementing the
     * {@link com.rocket.summer.framework.core.Ordered} interface (only
     * checked for singleton beans), or an order expressed through the
     * {@link com.rocket.summer.framework.core.annotation.Order} annotation
     * at the class level.
     * @see com.rocket.summer.framework.core.Ordered
     * @see com.rocket.summer.framework.core.annotation.Order
     */
    @Override
    public int getOrder() {
        Class<?> type = this.beanFactory.getType(this.name);
        if (type != null) {
            if (Ordered.class.isAssignableFrom(type) && this.beanFactory.isSingleton(this.name)) {
                return ((Ordered) this.beanFactory.getBean(this.name)).getOrder();
            }
            return OrderUtils.getOrder(type, Ordered.LOWEST_PRECEDENCE);
        }
        return Ordered.LOWEST_PRECEDENCE;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + ": bean name '" + this.name + "'";
    }

}

