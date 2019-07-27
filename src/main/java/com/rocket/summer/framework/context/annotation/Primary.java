package com.rocket.summer.framework.context.annotation;

import java.lang.annotation.*;

/**
 * Indicates that a bean should be given preference when multiple candidates
 * are qualified to autowire a single-valued dependency. If exactly one 'primary'
 * bean exists among the candidates, it will be the autowired value.
 *
 * <p>May be used on any class directly or indirectly annotated with
 * {@link com.rocket.summer.framework.stereotype.Component} or on methods annotated
 * with {@link Bean}.
 *
 * <p>Using {@link Primary} at the class level has no effect unless component-scanning
 * is being used. If a {@link Primary}-annotated class is declared via XML,
 * {@link Primary} annotation metadata is ignored, and
 * {@literal <bean primary="true|false"/>} is respected instead.
 *
 * @author Chris Beams
 * @since 3.0
 * @see Lazy
 * @see Bean
 * @see com.rocket.summer.framework.stereotype.Component
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Primary {

}
