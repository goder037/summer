package com.rocket.summer.framework.aop.aspectj;

import org.aopalliance.aop.Advice;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.aop.AfterAdvice;
import com.rocket.summer.framework.aop.BeforeAdvice;

/**
 * Utility methods for dealing with AspectJ advisors.
 *
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AspectJAopUtils {

    /**
     * Return {@code true} if the advisor is a form of before advice.
     */
    public static boolean isBeforeAdvice(Advisor anAdvisor) {
        AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isBeforeAdvice();
        }
        return (anAdvisor.getAdvice() instanceof BeforeAdvice);
    }

    /**
     * Return {@code true} if the advisor is a form of after advice.
     */
    public static boolean isAfterAdvice(Advisor anAdvisor) {
        AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isAfterAdvice();
        }
        return (anAdvisor.getAdvice() instanceof AfterAdvice);
    }

    /**
     * Return the AspectJPrecedenceInformation provided by this advisor or its advice.
     * If neither the advisor nor the advice have precedence information, this method
     * will return {@code null}.
     */
    public static AspectJPrecedenceInformation getAspectJPrecedenceInformationFor(Advisor anAdvisor) {
        if (anAdvisor instanceof AspectJPrecedenceInformation) {
            return (AspectJPrecedenceInformation) anAdvisor;
        }
        Advice advice = anAdvisor.getAdvice();
        if (advice instanceof AspectJPrecedenceInformation) {
            return (AspectJPrecedenceInformation) advice;
        }
        return null;
    }

}
