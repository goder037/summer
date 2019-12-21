package com.rocket.summer.framework.aop.framework;

/**
 * Marker interface that indicates a bean that is part of Spring's
 * AOP infrastructure. In particular, this implies that any such bean
 * is not subject to auto-proxying, even if a pointcut would match.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see com.rocket.summer.framework.aop.framework.autoproxy.AbstractAutoProxyCreator
 * @see com.rocket.summer.framework.aop.scope.ScopedProxyFactoryBean
 */
public interface AopInfrastructureBean {

}
