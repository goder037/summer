package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.aop.MethodMatcher;
import com.rocket.summer.framework.aop.Pointcut;

/**
 * Convenient superclass when we want to force subclasses to implement the
 * {@link MethodMatcher} interface but subclasses will want to be pointcuts.
 *
 * <p>The {@link #setClassFilter "classFilter"} property can be set to customize
 * {@link ClassFilter} behavior. The default is {@link ClassFilter#TRUE}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class StaticMethodMatcherPointcut extends StaticMethodMatcher implements Pointcut {

    private ClassFilter classFilter = ClassFilter.TRUE;


    /**
     * Set the {@link ClassFilter} to use for this pointcut.
     * Default is {@link ClassFilter#TRUE}.
     */
    public void setClassFilter(ClassFilter classFilter) {
        this.classFilter = classFilter;
    }

    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }


    @Override
    public final MethodMatcher getMethodMatcher() {
        return this;
    }

}

