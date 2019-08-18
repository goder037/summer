package com.rocket.summer.framework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method as a candidate for <i>asynchronous</i> execution.
 * Can also be used at the type level, in which case all of the type's methods are
 * considered as asynchronous.
 *
 * <p>In terms of target method signatures, any parameter types are supported.
 * However, the return type is constrained to either {@code void} or
 * {@link java.util.concurrent.Future}. In the latter case, you may declare the
 * more specific {@link com.rocket.summer.framework.util.concurrent.ListenableFuture} or
 * {@link java.util.concurrent.CompletableFuture} types which allow for richer
 * interaction with the asynchronous task and for immediate composition with
 * further processing steps.
 *
 * <p>A {@code Future} handle returned from the proxy will be an actual asynchronous
 * {@code Future} that can be used to track the result of the asynchronous method
 * execution. However, since the target method needs to implement the same signature,
 * it will have to return a temporary {@code Future} handle that just passes a value
 * through: e.g. Spring's {@link AsyncResult}, EJB 3.1's {@link javax.ejb.AsyncResult},
 * or {@link java.util.concurrent.CompletableFuture#completedFuture(Object)}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 * @see AnnotationAsyncExecutionInterceptor
 * @see AsyncAnnotationAdvisor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {

    /**
     * A qualifier value for the specified asynchronous operation(s).
     * <p>May be used to determine the target executor to be used when executing this
     * method, matching the qualifier value (or the bean name) of a specific
     * {@link java.util.concurrent.Executor Executor} or
     * {@link com.rocket.summer.framework.core.task.TaskExecutor TaskExecutor}
     * bean definition.
     * <p>When specified on a class level {@code @Async} annotation, indicates that the
     * given executor should be used for all methods within the class. Method level use
     * of {@code Async#value} always overrides any value set at the class level.
     * @since 3.1.2
     */
    String value() default "";

}
