package com.rocket.summer.framework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.rocket.summer.framework.aop.MethodBeforeAdvice;

/**
 * Spring AOP advice that wraps an AspectJ before method.
 *
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice implements MethodBeforeAdvice, Serializable {

    public AspectJMethodBeforeAdvice(
            Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }


    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(getJoinPointMatch(), null, null);
    }

    @Override
    public boolean isBeforeAdvice() {
        return true;
    }

    @Override
    public boolean isAfterAdvice() {
        return false;
    }

}

