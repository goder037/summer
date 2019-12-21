package com.rocket.summer.framework.context.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.context.event.ApplicationEvent;
import com.rocket.summer.framework.core.annotation.AliasFor;

/**
 * Annotation that marks a method as a listener for application events.
 *
 * <p>If an annotated method supports a single event type, the method may
 * declare a single parameter that reflects the event type to listen to.
 * If an annotated method supports multiple event types, this annotation
 * may refer to one or more supported event types using the {@code classes}
 * attribute. See the {@link #classes} javadoc for further details.
 *
 * <p>Events can be {@link ApplicationEvent} instances as well as arbitrary
 * objects.
 *
 * <p>Processing of {@code @EventListener} annotations is performed via
 * the internal {@link EventListenerMethodProcessor} bean which gets
 * registered automatically when using Java config or manually via the
 * {@code <context:annotation-config/>} or {@code <context:component-scan/>}
 * element when using XML config.
 *
 * <p>Annotated methods may have a non-{@code void} return type. When they
 * do, the result of the method invocation is sent as a new event. If the
 * return type is either an array or a collection, each element is sent
 * as a new individual event.
 *
 * <p>It is also possible to define the order in which listeners for a
 * certain event are to be invoked. To do so, add Spring's common
 * {@link com.rocket.summer.framework.core.annotation.Order @Order} annotation
 * alongside this event listener annotation.
 *
 * <p>While it is possible for an event listener to declare that it
 * throws arbitrary exception types, any checked exceptions thrown
 * from an event listener will be wrapped in an
 * {@link java.lang.reflect.UndeclaredThrowableException}
 * since the event publisher can only handle runtime exceptions.
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see EventListenerMethodProcessor
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventListener {

    /**
     * Alias for {@link #classes}.
     */
    @AliasFor("classes")
    Class<?>[] value() default {};

    /**
     * The event classes that this listener handles.
     * <p>If this attribute is specified with a single value, the
     * annotated method may optionally accept a single parameter.
     * However, if this attribute is specified with multiple values,
     * the annotated method must <em>not</em> declare any parameters.
     */
    @AliasFor("value")
    Class<?>[] classes() default {};

    /**
     * Spring Expression Language (SpEL) attribute used for making the
     * event handling conditional.
     * <p>Default is {@code ""}, meaning the event is always handled.
     * <p>The SpEL expression evaluates against a dedicated context that
     * provides the following meta-data:
     * <ul>
     * <li>{@code #root.event}, {@code #root.args} for
     * references to the {@link ApplicationEvent} and method arguments
     * respectively.</li>
     * <li>Method arguments can be accessed by index. For instance the
     * first argument can be accessed via {@code #root.args[0]}, {@code #p0}
     * or {@code #a0}. Arguments can also be accessed by name if that
     * information is available.</li>
     * </ul>
     */
    String condition() default "";

}

