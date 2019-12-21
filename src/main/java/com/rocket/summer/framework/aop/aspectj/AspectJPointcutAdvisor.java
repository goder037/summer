package com.rocket.summer.framework.aop.aspectj;

import org.aopalliance.aop.Advice;

import com.rocket.summer.framework.aop.Pointcut;
import com.rocket.summer.framework.aop.PointcutAdvisor;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.Assert;

/**
 * AspectJPointcutAdvisor that adapts an {@link AbstractAspectJAdvice}
 * to the {@link com.rocket.summer.framework.aop.PointcutAdvisor} interface.
 *
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @since 2.0
 */
public class AspectJPointcutAdvisor implements PointcutAdvisor, Ordered {

    private final AbstractAspectJAdvice advice;

    private final Pointcut pointcut;

    private Integer order;


    /**
     * Create a new AspectJPointcutAdvisor for the given advice
     * @param advice the AbstractAspectJAdvice to wrap
     */
    public AspectJPointcutAdvisor(AbstractAspectJAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        this.pointcut = advice.buildSafePointcut();
    }


    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public int getOrder() {
        if (this.order != null) {
            return this.order;
        }
        else {
            return this.advice.getOrder();
        }
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AspectJPointcutAdvisor)) {
            return false;
        }
        AspectJPointcutAdvisor otherAdvisor = (AspectJPointcutAdvisor) other;
        return this.advice.equals(otherAdvisor.advice);
    }

    @Override
    public int hashCode() {
        return AspectJPointcutAdvisor.class.hashCode() * 29 + this.advice.hashCode();
    }

}

