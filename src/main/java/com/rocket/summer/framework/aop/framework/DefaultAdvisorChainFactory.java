package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.aop.IntroductionAdvisor;
import com.rocket.summer.framework.aop.MethodMatcher;
import com.rocket.summer.framework.aop.PointcutAdvisor;
import com.rocket.summer.framework.aop.framework.adapter.AdvisorAdapterRegistry;
import com.rocket.summer.framework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import com.rocket.summer.framework.aop.support.MethodMatchers;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple but definitive way of working out an advice chain for a Method,
 * given an {@link Advised} object. Always rebuilds each advice chain;
 * caching can be provided by subclasses.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0.3
 */
public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable {

    public List getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, Class targetClass) {
        // This is somewhat tricky... we have to process introductions first,
        // but we need to preserve order in the ultimate list.
        List interceptorList = new ArrayList(config.getAdvisors().length);
        boolean hasIntroductions = hasMatchingIntroductions(config, targetClass);
        AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
        Advisor[] advisors = config.getAdvisors();
        for (int i = 0; i < advisors.length; i++) {
            Advisor advisor = advisors[i];
            if (advisor instanceof PointcutAdvisor) {
                // Add it conditionally.
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(targetClass)) {
                    MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
                    MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                    if (MethodMatchers.matches(mm, method, targetClass, hasIntroductions)) {
                        if (mm.isRuntime()) {
                            // Creating a new object instance in the getInterceptors() method
                            // isn't a problem as we normally cache created chains.
                            for (int j = 0; j < interceptors.length; j++) {
                                interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptors[j], mm));
                            }
                        }
                        else {
                            interceptorList.addAll(Arrays.asList(interceptors));
                        }
                    }
                }
            }
            else if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
                if (config.isPreFiltered() || ia.getClassFilter().matches(targetClass)) {
                    Interceptor[] interceptors = registry.getInterceptors(advisor);
                    interceptorList.addAll(Arrays.asList(interceptors));
                }
            }
            else {
                Interceptor[] interceptors = registry.getInterceptors(advisor);
                interceptorList.addAll(Arrays.asList(interceptors));
            }
        }
        return interceptorList;
    }

    /**
     * Determine whether the Advisors contain matching introductions.
     */
    private static boolean hasMatchingIntroductions(Advised config, Class targetClass) {
        for (int i = 0; i < config.getAdvisors().length; i++) {
            Advisor advisor = config.getAdvisors()[i];
            if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
                if (ia.getClassFilter().matches(targetClass)) {
                    return true;
                }
            }
        }
        return false;
    }

}

