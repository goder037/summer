package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.*;

/**
 * Annotation which indicates that a method parameter should be bound to a web request header.
 * Supported for annotated handler methods in Servlet and Portlet environments.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestMapping
 * @see RequestParam
 * @see CookieValue
 * @see com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerMethodAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestHeader {

    /**
     * The name of the request header to bind to.
     */
    String value() default "";

    /**
     * Whether the header is required.
     * <p>Default is <code>true</code>, leading to an exception thrown in case
     * of the header missing in the request. Switch this to <code>false</code>
     * if you prefer a <code>null</value> in case of the header missing.
     * <p>Alternatively, provide a {@link #defaultValue() defaultValue},
     * which implicitely sets this flag to <code>false</code>.
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback. Supplying a default value implicitely
     * sets {@link #required()} to false.
     */
    String defaultValue() default ValueConstants.DEFAULT_NONE;

}