package com.rocket.summer.framework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.weaver.tools.JoinPointMatch;

import com.rocket.summer.framework.aop.ProxyMethodInvocation;

/**
 * Spring AOP around advice (MethodInterceptor) that wraps
 * an AspectJ advice method. Exposes ProceedingJoinPoint.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJAroundAdvice extends AbstractAspectJAdvice implements MethodInterceptor, Serializable {

    public AspectJAroundAdvice(
            Method aspectJAroundAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

        super(aspectJAroundAdviceMethod, pointcut, aif);
    }


    @Override
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override
    public boolean isAfterAdvice() {
        return false;
    }

    @Override
    protected boolean supportsProceedingJoinPoint() {
        return true;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
        ProceedingJoinPoint pjp = lazyGetProceedingJoinPoint(pmi);
        JoinPointMatch jpm = getJoinPointMatch(pmi);
        return invokeAdviceMethod(pjp, jpm, null, null);
    }

    /**
     * Return the ProceedingJoinPoint for the current invocation,
     * instantiating it lazily if it hasn't been bound to the thread already.
     * @param rmi the current Spring AOP ReflectiveMethodInvocation,
     * which we'll use for attribute binding
     * @return the ProceedingJoinPoint to make available to advice methods
     */
    protected ProceedingJoinPoint lazyGetProceedingJoinPoint(ProxyMethodInvocation rmi) {
        return new MethodInvocationProceedingJoinPoint(rmi);
    }

}

