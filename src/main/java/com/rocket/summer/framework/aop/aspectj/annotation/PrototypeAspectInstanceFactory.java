package com.rocket.summer.framework.aop.aspectj.annotation;

import java.io.Serializable;

import com.rocket.summer.framework.beans.factory.BeanFactory;

/**
 * {@link com.rocket.summer.framework.aop.aspectj.AspectInstanceFactory} backed by a
 * {@link BeanFactory}-provided prototype, enforcing prototype semantics.
 *
 * <p>Note that this may instantiate multiple times, which probably won't give the
 * semantics you expect. Use a {@link LazySingletonAspectInstanceFactoryDecorator}
 * to wrap this to ensure only one new aspect comes back.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.beans.factory.BeanFactory
 * @see LazySingletonAspectInstanceFactoryDecorator
 */
public class PrototypeAspectInstanceFactory extends BeanFactoryAspectInstanceFactory implements Serializable {

    /**
     * Create a PrototypeAspectInstanceFactory. AspectJ will be called to
     * introspect to create AJType metadata using the type returned for the
     * given bean name from the BeanFactory.
     * @param beanFactory the BeanFactory to obtain instance(s) from
     * @param name the name of the bean
     */
    public PrototypeAspectInstanceFactory(BeanFactory beanFactory, String name) {
        super(beanFactory, name);
        if (!beanFactory.isPrototype(name)) {
            throw new IllegalArgumentException(
                    "Cannot use PrototypeAspectInstanceFactory with bean named '" + name + "': not a prototype");
        }
    }

}

