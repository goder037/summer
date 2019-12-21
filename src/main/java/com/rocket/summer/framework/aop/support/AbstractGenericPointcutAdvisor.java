package com.rocket.summer.framework.aop.support;

import org.aopalliance.aop.Advice;

/**
 * Abstract generic PointcutAdvisor that allows for any Advice to be configured.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #setAdvice
 * @see DefaultPointcutAdvisor
 */
public abstract class AbstractGenericPointcutAdvisor extends AbstractPointcutAdvisor {

    private Advice advice;


    /**
     * Specify the advice that this advisor should apply.
     */
    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public Advice getAdvice() {
        return this.advice;
    }


    public String toString() {
        return getClass().getName() + ": advice [" + getAdvice() + "]";
    }

}

