package com.rocket.summer.framework.aop;

/**
 * Marker interface implemented by all AOP proxies. Used to detect
 * whether or not objects are Spring-generated proxies.
 *
 * @author Rob Harrop
 * @since 2.0.1
 * @see com.rocket.summer.framework.aop.support.AopUtils#isAopProxy(Object)
 */
public interface SpringProxy {

}
