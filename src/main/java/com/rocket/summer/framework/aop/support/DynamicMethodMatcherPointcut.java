package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.aop.MethodMatcher;
import com.rocket.summer.framework.aop.Pointcut;

/**
 * Convenient superclass when we want to force subclasses to
 * implement MethodMatcher interface, but subclasses
 * will want to be pointcuts. The getClassFilter() method can
 * be overridden to customize ClassFilter behaviour as well.
 *
 * @author Rod Johnson
 */
public abstract class DynamicMethodMatcherPointcut extends DynamicMethodMatcher implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public final MethodMatcher getMethodMatcher() {
        return this;
    }

}

