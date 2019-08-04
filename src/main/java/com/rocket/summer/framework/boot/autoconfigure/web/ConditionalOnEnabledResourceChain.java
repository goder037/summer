package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * {@link Conditional} that checks whether or not the Spring resource handling chain is
 * enabled. Matches if {@link ResourceProperties.Chain#getEnabled()} is {@code true} or if
 * {@code webjars-locator} is on the classpath.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnEnabledResourceChainCondition.class)
public @interface ConditionalOnEnabledResourceChain {

}
