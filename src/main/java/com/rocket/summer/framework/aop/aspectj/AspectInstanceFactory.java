package com.rocket.summer.framework.aop.aspectj;

import com.rocket.summer.framework.core.Ordered;

/**
 * Interface implemented to provide an instance of an AspectJ aspect.
 * Decouples from Spring's bean factory.
 *
 * <p>Extends the {@link com.rocket.summer.framework.core.Ordered} interface
 * to express an order value for the underlying aspect in a chain.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.beans.factory.BeanFactory#getBean
 */
public interface AspectInstanceFactory extends Ordered {

    /**
     * Create an instance of this factory's aspect.
     * @return the aspect instance (never {@code null})
     */
    Object getAspectInstance();

    /**
     * Expose the aspect class loader that this factory uses.
     * @return the aspect class loader (never {@code null})
     */
    ClassLoader getAspectClassLoader();

}
