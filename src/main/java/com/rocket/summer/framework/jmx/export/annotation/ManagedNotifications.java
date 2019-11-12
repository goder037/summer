package com.rocket.summer.framework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Type-level annotation that indicates JMX notifications emitted by a bean,
 * containing multiple {@link ManagedNotification ManagedNotifications}.
 *
 * @author Rob Harrop
 * @since 2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ManagedNotifications {

    ManagedNotification[] value();

}

