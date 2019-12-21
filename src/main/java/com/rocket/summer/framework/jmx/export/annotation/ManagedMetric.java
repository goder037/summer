package com.rocket.summer.framework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.jmx.support.MetricType;

/**
 * Method-level annotation that indicates to expose a given bean property as a
 * JMX attribute, with added descriptor properties to indicate that it is a metric.
 * Only valid when used on a JavaBean getter.
 *
 * @author Jennifer Hickey
 * @since 3.0
 * @see com.rocket.summer.framework.jmx.export.metadata.ManagedMetric
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedMetric {

    String category() default "";

    int currencyTimeLimit() default -1;

    String description() default "";

    String displayName() default "";

    MetricType metricType() default MetricType.GAUGE;

    int persistPeriod() default -1;

    String persistPolicy() default "";

    String unit() default "";

}

