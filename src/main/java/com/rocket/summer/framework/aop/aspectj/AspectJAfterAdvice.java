package com.rocket.summer.framework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.rocket.summer.framework.aop.AfterAdvice;

/**
 * Spring AOP advice wrapping an AspectJ after advice method.
 *
 * @author Rod Johnson
 * @since 2.0
 */
public class AspectJAfterAdvice extends AbstractAspectJAdvice
        implements MethodInterceptor, AfterAdvice, Serializable {

    public AspectJAfterAdvice(
            Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }
        finally {
            invokeAdviceMethod(getJoinPointMatch(), null, null);
        }
    }

    @Override
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override
    public boolean isAfterAdvice() {
        return true;
    }

}

