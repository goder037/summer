package com.rocket.summer.framework.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.aop.Advice;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.aop.aspectj.AspectJExpressionPointcut;
import com.rocket.summer.framework.aop.framework.AopConfigException;

/**
 * Interface for factories that can create Spring AOP Advisors from classes
 * annotated with AspectJ annotation syntax.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjTypeSystem
 */
public interface AspectJAdvisorFactory {

    /**
     * Determine whether or not the given class is an aspect, as reported
     * by AspectJ's {@link org.aspectj.lang.reflect.AjTypeSystem}.
     * <p>Will simply return {@code false} if the supposed aspect is
     * invalid (such as an extension of a concrete aspect class).
     * Will return true for some aspects that Spring AOP cannot process,
     * such as those with unsupported instantiation models.
     * Use the {@link #validate} method to handle these cases if necessary.
     * @param clazz the supposed annotation-style AspectJ class
     * @return whether or not this class is recognized by AspectJ as an aspect class
     */
    boolean isAspect(Class<?> clazz);

    /**
     * Is the given class a valid AspectJ aspect class?
     * @param aspectClass the supposed AspectJ annotation-style class to validate
     * @throws AopConfigException if the class is an invalid aspect
     * (which can never be legal)
     * @throws NotAnAtAspectException if the class is not an aspect at all
     * (which may or may not be legal, depending on the context)
     */
    void validate(Class<?> aspectClass) throws AopConfigException;

    /**
     * Build Spring AOP Advisors for all annotated At-AspectJ methods
     * on the specified aspect instance.
     * @param aspectInstanceFactory the aspect instance factory
     * (not the aspect instance itself in order to avoid eager instantiation)
     * @return a list of advisors for this class
     */
    List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory);

    /**
     * Build a Spring AOP Advisor for the given AspectJ advice method.
     * @param candidateAdviceMethod the candidate advice method
     * @param aspectInstanceFactory the aspect instance factory
     * @param declarationOrder the declaration order within the aspect
     * @param aspectName the name of the aspect
     * @return {@code null} if the method is not an AspectJ advice method
     * or if it is a pointcut that will be used by other advice but will not
     * create a Spring advice in its own right
     */
    Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
                       int declarationOrder, String aspectName);

    /**
     * Build a Spring AOP Advice for the given AspectJ advice method.
     * @param candidateAdviceMethod the candidate advice method
     * @param expressionPointcut the AspectJ expression pointcut
     * @param aspectInstanceFactory the aspect instance factory
     * @param declarationOrder the declaration order within the aspect
     * @param aspectName the name of the aspect
     * @return {@code null} if the method is not an AspectJ advice method
     * or if it is a pointcut that will be used by other advice but will not
     * create a Spring advice in its own right
     * @see com.rocket.summer.framework.aop.aspectj.AspectJAroundAdvice
     * @see com.rocket.summer.framework.aop.aspectj.AspectJMethodBeforeAdvice
     * @see com.rocket.summer.framework.aop.aspectj.AspectJAfterAdvice
     * @see com.rocket.summer.framework.aop.aspectj.AspectJAfterReturningAdvice
     * @see com.rocket.summer.framework.aop.aspectj.AspectJAfterThrowingAdvice
     */
    Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
                     MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);

}
