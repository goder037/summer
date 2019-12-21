package com.rocket.summer.framework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Type-level annotation that indicates a JMX notification emitted by a bean.
 *
 * <p>As of Spring Framework 4.2.4, this annotation is declared as repeatable.
 *
 * @author Rob Harrop
 * @since 2.0
 * @see com.rocket.summer.framework.jmx.export.metadata.ManagedNotification
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Repeatable(ManagedNotifications.class)
public @interface ManagedNotification {

    String name();

    String description() default "";

    String[] notificationTypes();

}

