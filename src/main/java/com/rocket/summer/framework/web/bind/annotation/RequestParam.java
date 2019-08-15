package com.rocket.summer.framework.web.bind.annotation;

import com.rocket.summer.framework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.Map;

/**
 * Annotation which indicates that a method parameter should be bound to a web
 * request parameter.
 *
 * <p>Supported for annotated handler methods in Servlet and Portlet environments.
 *
 * <p>If the method parameter type is {@link Map} and a request parameter name
 * is specified, then the request parameter value is converted to a {@link Map}
 * assuming an appropriate conversion strategy is available.
 *
 * <p>If the method parameter is {@link java.util.Map Map&lt;String, String&gt;} or
 * {@link com.rocket.summer.framework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
 * and a parameter name is not specified, then the map parameter is populated
 * with all request parameter names and values.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 2.5
 * @see RequestMapping
 * @see RequestHeader
 * @see CookieValue
 * @see com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the request parameter to bind to.
     * @since 4.2
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether the parameter is required.
     * <p>Defaults to {@code true}, leading to an exception being thrown
     * if the parameter is missing in the request. Switch this to
     * {@code false} if you prefer a {@code null} value if the parameter is
     * not present in the request.
     * <p>Alternatively, provide a {@link #defaultValue}, which implicitly
     * sets this flag to {@code false}.
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback when the request parameter is
     * not provided or has an empty value.
     * <p>Supplying a default value implicitly sets {@link #required} to
     * {@code false}.
     */
    String defaultValue() default ValueConstants.DEFAULT_NONE;

}
