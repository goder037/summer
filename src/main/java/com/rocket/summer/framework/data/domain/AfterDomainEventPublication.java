package com.rocket.summer.framework.data.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on a method of a Spring Data managed aggregate to get invoked after the events of an aggregate
 * have been published.
 *
 * @author Oliver Gierke
 * @see DomainEvents
 * @since 1.13
 * @soundtrack Benny Greb - September (Moving Parts Live)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface AfterDomainEventPublication {

}
