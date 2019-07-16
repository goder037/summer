package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.Pointcut;
import org.aopalliance.aop.Advice;

import java.io.Serializable;

/**
 * Convenient Pointcut-driven Advisor implementation.
 *
 * <p>This is the most commonly used Advisor implementation. It can be used
 * with any pointcut and advice type, except for introductions. There is
 * normally no need to subclass this class, or to implement custom Advisors.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setPointcut
 * @see #setAdvice
 */
public class DefaultPointcutAdvisor extends AbstractGenericPointcutAdvisor implements Serializable {

    private Pointcut pointcut = Pointcut.TRUE;


    /**
     * Create an empty DefaultPointcutAdvisor.
     * <p>Advice must be set before use using setter methods.
     * Pointcut will normally be set also, but defaults to <code>Pointcut.TRUE</code>.
     */
    public DefaultPointcutAdvisor() {
    }

    /**
     * Create a DefaultPointcutAdvisor that matches all methods.
     * <p><code>Pointcut.TRUE</code> will be used as Pointcut.
     * @param advice the Advice to use
     */
    public DefaultPointcutAdvisor(Advice advice) {
        this(Pointcut.TRUE, advice);
    }

    /**
     * Create a DefaultPointcutAdvisor, specifying Pointcut and Advice.
     * @param pointcut the Pointcut targeting the Advice
     * @param advice the Advice to run when Pointcut matches
     */
    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        setAdvice(advice);
    }


    /**
     * Specify the pointcut targeting the advice.
     * <p>Default is <code>Pointcut.TRUE</code>.
     * @see #setAdvice
     */
    public void setPointcut(Pointcut pointcut) {
        this.pointcut = (pointcut != null ? pointcut : Pointcut.TRUE);
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }


    public String toString() {
        return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice [" + getAdvice() + "]";
    }

}

