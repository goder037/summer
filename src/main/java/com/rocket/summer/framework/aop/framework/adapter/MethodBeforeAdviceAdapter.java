package com.rocket.summer.framework.aop.framework.adapter;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.aop.MethodBeforeAdvice;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.io.Serializable;

/**
 * Adapter to enable {@link org.springframework.aop.MethodBeforeAdvice}
 * to be used in the Spring AOP framework.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
class MethodBeforeAdviceAdapter implements AdvisorAdapter, Serializable {

    public boolean supportsAdvice(Advice advice) {
        return (advice instanceof MethodBeforeAdvice);
    }

    public MethodInterceptor getInterceptor(Advisor advisor) {
        MethodBeforeAdvice advice = (MethodBeforeAdvice) advisor.getAdvice();
        return new MethodBeforeAdviceInterceptor(advice);
    }

}
