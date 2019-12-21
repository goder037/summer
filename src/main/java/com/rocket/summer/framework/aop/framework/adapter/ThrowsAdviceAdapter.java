package com.rocket.summer.framework.aop.framework.adapter;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.aop.ThrowsAdvice;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.io.Serializable;

/**
 * Adapter to enable {@link com.rocket.summer.framework.aop.MethodBeforeAdvice}
 * to be used in the Spring AOP framework.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
class ThrowsAdviceAdapter implements AdvisorAdapter, Serializable {

    public boolean supportsAdvice(Advice advice) {
        return (advice instanceof ThrowsAdvice);
    }

    public MethodInterceptor getInterceptor(Advisor advisor) {
        return new ThrowsAdviceInterceptor(advisor.getAdvice());
    }

}