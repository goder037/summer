package com.rocket.summer.framework.data.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.context.ApplicationEventPublisher;

/**
 * {@link DomainEvents} can be used on methods of aggregate roots managed by Spring Data repositories to publish the
 * events returned by that method as Spring application events.
 *
 * @author Oliver Gierke
 * @see ApplicationEventPublisher
 * @see AfterDomainEventPublication
 * @since 1.13
 * @soundtrack Benny Greb - Soulfood (Moving Parts Live)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface DomainEvents {
}