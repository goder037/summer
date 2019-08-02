package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * {@link Conditional} that only matches when the specified bean class is already
 * contained in the {@link BeanFactory} and a single candidate can be determined.
 * <p>
 * The condition will also match if multiple matching bean instances are already contained
 * in the {@link BeanFactory} but a primary candidate has been defined; essentially, the
 * condition match if auto-wiring a bean with the defined type will succeed.
 * <p>
 * The condition can only match the bean definitions that have been processed by the
 * application context so far and, as such, it is strongly recommended to use this
 * condition on auto-configuration classes only. If a candidate bean may be created by
 * another auto-configuration, make sure that the one using this condition runs after.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnBeanCondition.class)
public @interface ConditionalOnSingleCandidate {

    /**
     * The class type of bean that should be checked. The condition match if the class
     * specified is contained in the {@link ApplicationContext} and a primary candidate
     * exists in case of multiple instances.
     * <p>
     * This attribute may <strong>not</strong> be used in conjunction with
     * {@link #type()}, but it may be used instead of {@link #type()}.
     * @return the class type of the bean to check
     */
    Class<?> value() default Object.class;

    /**
     * The class type name of bean that should be checked. The condition matches if the
     * class specified is contained in the {@link ApplicationContext} and a primary
     * candidate exists in case of multiple instances.
     * <p>
     * This attribute may <strong>not</strong> be used in conjunction with
     * {@link #value()}, but it may be used instead of {@link #value()}.
     * @return the class type name of the bean to check
     */
    String type() default "";

    /**
     * Strategy to decide if the application context hierarchy (parent contexts) should be
     * considered.
     * @return the search strategy
     */
    SearchStrategy search() default SearchStrategy.ALL;

}
