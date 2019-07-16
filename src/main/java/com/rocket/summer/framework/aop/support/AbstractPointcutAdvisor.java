package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.PointcutAdvisor;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.ObjectUtils;

import java.io.Serializable;

/**
 * Abstract base class for {@link org.springframework.aop.PointcutAdvisor}
 * implementations. Can be subclassed for returning a specific pointcut/advice
 * or a freely configurable pointcut/advice.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1.2
 * @see AbstractGenericPointcutAdvisor
 */
public abstract class AbstractPointcutAdvisor implements PointcutAdvisor, Ordered, Serializable {

    private int order = Ordered.LOWEST_PRECEDENCE;


    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public boolean isPerInstance() {
        return true;
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PointcutAdvisor)) {
            return false;
        }
        PointcutAdvisor otherAdvisor = (PointcutAdvisor) other;
        return (ObjectUtils.nullSafeEquals(getAdvice(), otherAdvisor.getAdvice()) &&
                ObjectUtils.nullSafeEquals(getPointcut(), otherAdvisor.getPointcut()));
    }

    public int hashCode() {
        return PointcutAdvisor.class.hashCode();
    }

}

