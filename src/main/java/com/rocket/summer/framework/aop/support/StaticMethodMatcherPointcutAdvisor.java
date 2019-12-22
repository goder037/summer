package com.rocket.summer.framework.aop.support;

import java.io.Serializable;

import org.aopalliance.aop.Advice;

import com.rocket.summer.framework.aop.Pointcut;
import com.rocket.summer.framework.aop.PointcutAdvisor;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.Assert;

/**
 * Convenient base class for Advisors that are also static pointcuts.
 * Serializable if Advice and subclass are.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public abstract class StaticMethodMatcherPointcutAdvisor extends StaticMethodMatcherPointcut
        implements PointcutAdvisor, Ordered, Serializable {

    private Advice advice;

    private int order = Integer.MAX_VALUE;


    /**
     * Create a new StaticMethodMatcherPointcutAdvisor,
     * expecting bean-style configuration.
     * @see #setAdvice
     */
    public StaticMethodMatcherPointcutAdvisor() {
    }

    /**
     * Create a new StaticMethodMatcherPointcutAdvisor for the given advice.
     * @param advice the Advice to use
     */
    public StaticMethodMatcherPointcutAdvisor(Advice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
    }


    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public Pointcut getPointcut() {
        return this;
    }

}

