package com.rocket.summer.framework.aop;

/**
 * Core Spring pointcut abstraction.
 *
 * <p>A pointcut is composed of a {@link ClassFilter} and a {@link MethodMatcher}.
 * Both these basic terms and a Pointcut itself can be combined to build up combinations
 * (e.g. through {@link com.rocket.summer.framework.aop.support.ComposablePointcut}).
 *
 * @author Rod Johnson
 * @see ClassFilter
 * @see MethodMatcher
 * @see com.rocket.summer.framework.aop.support.Pointcuts
 * @see com.rocket.summer.framework.aop.support.ClassFilters
 * @see com.rocket.summer.framework.aop.support.MethodMatchers
 */
public interface Pointcut {

    /**
     * Return the ClassFilter for this pointcut.
     * @return the ClassFilter (never <code>null</code>)
     */
    ClassFilter getClassFilter();

    /**
     * Return the MethodMatcher for this pointcut.
     * @return the MethodMatcher (never <code>null</code>)
     */
    MethodMatcher getMethodMatcher();


    /**
     * Canonical Pointcut instance that always matches.
     */
    Pointcut TRUE = TruePointcut.INSTANCE;

}

