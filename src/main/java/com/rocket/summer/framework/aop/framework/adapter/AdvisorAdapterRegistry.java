package com.rocket.summer.framework.aop.framework.adapter;

import com.rocket.summer.framework.aop.Advisor;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Interface for registries of Advisor adapters.
 *
 * <p><i>This is an SPI interface, not to be implemented by any Spring user.</i>
 *
 * @author Rod Johnson
 * @author Rob Harrop
 */
public interface AdvisorAdapterRegistry {

    /**
     * Return an Advisor wrapping the given advice.
     * <p>Should by default at least support
     * {@link org.aopalliance.intercept.MethodInterceptor},
     * {@link com.rocket.summer.framework.aop.MethodBeforeAdvice},
     * {@link com.rocket.summer.framework.aop.AfterReturningAdvice},
     * {@link com.rocket.summer.framework.aop.ThrowsAdvice}.
     * @param advice object that should be an advice
     * @return an Advisor wrapping the given advice. Never returns <code>null</code>.
     * If the advice parameter is an Advisor, return it.
     * @throws UnknownAdviceTypeException if no registered advisor adapter
     * can wrap the supposed advice
     */
    Advisor wrap(Object advice) throws UnknownAdviceTypeException;

    /**
     * Return an array of AOP Alliance MethodInterceptors to allow use of the
     * given Advisor in an interception-based framework.
     * <p>Don't worry about the pointcut associated with the Advisor,
     * if it's a PointcutAdvisor: just return an interceptor.
     * @param advisor Advisor to find an interceptor for
     * @return an array of MethodInterceptors to expose this Advisor's behavior
     * @throws UnknownAdviceTypeException if the Advisor type is
     * not understood by any registered AdvisorAdapter.
     */
    MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException;

    /**
     * Register the given AdvisorAdapter. Note that it is not necessary to register
     * adapters for an AOP Alliance Interceptors or Spring Advices: these must be
     * automatically recognized by an AdvisorAdapterRegistry implementation.
     * @param adapter AdvisorAdapter that understands a particular Advisor
     * or Advice types
     */
    void registerAdvisorAdapter(AdvisorAdapter adapter);

}

