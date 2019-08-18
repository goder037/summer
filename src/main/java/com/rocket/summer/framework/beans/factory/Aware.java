package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.context.support.ApplicationContextAwareProcessor;

/**
 * Marker superinterface indicating that a bean is eligible to be
 * notified by the Spring container of a particular framework object
 * through a callback-style method.  Actual method signature is
 * determined by individual subinterfaces, but should typically
 * consist of just one void-returning method that accepts a single
 * argument.
 *
 * <p>Note that merely implementing {@link Aware} provides no default
 * functionality. Rather, processing must be done explicitly, for example
 * in a {@link com.rocket.summer.framework.beans.factory.config.BeanPostProcessor BeanPostProcessor}.
 * Refer to {@link ApplicationContextAwareProcessor}
 * and {@link com.rocket.summer.framework.beans.factory.support.AbstractAutowireCapableBeanFactory}
 * for examples of processing {@code *Aware} interface callbacks.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface Aware {

}
