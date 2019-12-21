package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.core.annotation.AliasFor;

/**
 * Annotation which indicates that a method parameter should be bound to a URI template
 * variable. Supported for {@link RequestMapping} annotated handler methods in Servlet
 * environments.
 *
 * <p>If the method parameter is {@link java.util.Map Map&lt;String, String&gt;}
 * then the map is populated with all path variable names and values.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestMapping
 * @see com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the path variable to bind to.
     * @since 4.3.3
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether the path variable is required.
     * <p>Defaults to {@code true}, leading to an exception being thrown if the path
     * variable is missing in the incoming request. Switch this to {@code false} if
     * you prefer a {@code null} or Java 8 {@code java.util.Optional} in this case.
     * e.g. on a {@code ModelAttribute} method which serves for different requests.
     * @since 4.3.3
     */
    boolean required() default true;

}


