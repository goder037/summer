package com.rocket.summer.framework.beans.factory.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanFactoryPostProcessor;

import java.lang.annotation.*;

/**
 * Annotation at the field or method/constructor parameter level
 * that indicates a default value expression for the affected argument.
 *
 * <p>Typically used for expression-driven dependency injection. Also supported
 * for dynamic resolution of handler method parameters, e.g. in Spring MVC.
 *
 * <p>A common use case is to assign default field values using
 * "#{systemProperties.myProp}" style expressions.
 *
 * <p>Note that actual processing of the {@code @Value} annotation is performed
 * by a {@link com.rocket.summer.framework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} which in turn means that you <em>cannot</em> use
 * {@code @Value} within
 * {@link com.rocket.summer.framework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} or {@link BeanFactoryPostProcessor} types. Please
 * consult the javadoc for the {@link AutowiredAnnotationBeanPostProcessor}
 * class (which, by default, checks for the presence of this annotation).
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Autowired
 * @see com.rocket.summer.framework.beans.factory.config.BeanExpressionResolver
 * @see com.rocket.summer.framework.beans.factory.support.AutowireCandidateResolver#getSuggestedValue
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * The actual value expression: e.g. "#{systemProperties.myProp}".
     */
    String value();

}

