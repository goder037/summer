package com.rocket.summer.framework.aop.framework.adapter;

import com.rocket.summer.framework.aop.Advisor;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Interface allowing extension to the Spring AOP framework to allow
 * handling of new Advisors and Advice types.
 *
 * <p>Implementing objects can create AOP Alliance Interceptors from
 * custom advice types, enabling these advice types to be used
 * in the Spring AOP framework, which uses interception under the covers.
 *
 * <p>There is no need for most Spring users to implement this interface;
 * do so only if you need to introduce more Advisor or Advice types to Spring.
 *
 * @author Rod Johnson
 */
public interface AdvisorAdapter {

    /**
     * Does this adapter understand this advice object? Is it valid to
     * invoke the <code>getInterceptors</code> method with an Advisor that
     * contains this advice as an argument?
     * @param advice an Advice such as a BeforeAdvice
     * @return whether this adapter understands the given advice object
     * @see #getInterceptor(com.rocket.summer.framework.aop.Advisor)
     * @see com.rocket.summer.framework.aop.BeforeAdvice
     */
    boolean supportsAdvice(Advice advice);

    /**
     * Return an AOP Alliance MethodInterceptor exposing the behavior of
     * the given advice to an interception-based AOP framework.
     * <p>Don't worry about any Pointcut contained in the Advisor;
     * the AOP framework will take care of checking the pointcut.
     * @param advisor the Advisor. The supportsAdvice() method must have
     * returned true on this object
     * @return an AOP Alliance interceptor for this Advisor. There's
     * no need to cache instances for efficiency, as the AOP framework
     * caches advice chains.
     */
    MethodInterceptor getInterceptor(Advisor advisor);

}

