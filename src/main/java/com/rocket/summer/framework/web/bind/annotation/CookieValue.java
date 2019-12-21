package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.*;

/**
 * Annotation which indicates that a method parameter should be bound to an HTTP cookie.
 * Supported for annotated handler methods in Servlet and Portlet environments.
 *
 * <p>The method parameter may be declared as type {@link javax.servlet.http.Cookie}
 * or as cookie value type (String, int, etc).
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestMapping
 * @see RequestParam
 * @see RequestHeader
 * @see com.rocket.summer.framework.web.bind.annotation.RequestMapping
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieValue {

    /**
     * The name of the cookie to bind to.
     */
    String value() default "";

    /**
     * Whether the header is required.
     * <p>Default is <code>true</code>, leading to an exception being thrown
     * in case the header is missing in the request. Switch this to
     * <code>false</code> if you prefer a <code>null</value> in case of the
     * missing header.
     * <p>Alternatively, provide a {@link #defaultValue() defaultValue},
     * which implicitly sets this flag to <code>false</code>.
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback. Supplying a default value implicitly
     * sets {@link #required()} to false.
     */
    String defaultValue() default ValueConstants.DEFAULT_NONE;

}