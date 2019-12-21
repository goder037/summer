package com.rocket.summer.framework.aop.aspectj;

import com.rocket.summer.framework.aop.PointcutAdvisor;

/**
 * Interface to be implemented by Spring AOP Advisors wrapping AspectJ
 * aspects that may have a lazy initialization strategy. For example,
 * a perThis instantiation model would mean lazy initialization of the advice.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
public interface InstantiationModelAwarePointcutAdvisor extends PointcutAdvisor {

    /**
     * Return whether this advisor is lazily initializing its underlying advice.
     */
    boolean isLazy();

    /**
     * Return whether this advisor has already instantiated its advice.
     */
    boolean isAdviceInstantiated();

}

