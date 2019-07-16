package com.rocket.summer.framework.aop.framework;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Factory interface for advisor chains.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AdvisorChainFactory {

    /**
     * Determine a list of {@link org.aopalliance.intercept.MethodInterceptor} objects
     * for the given advisor chain configuration.
     * @param config the AOP configuration in the form of an Advised object
     * @param method the proxied method
     * @param targetClass the target class
     * @return List of MethodInterceptors (may also include InterceptorAndDynamicMethodMatchers)
     */
    List getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, Class targetClass);

}
