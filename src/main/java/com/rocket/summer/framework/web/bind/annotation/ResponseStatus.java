package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.core.annotation.AliasFor;
import com.rocket.summer.framework.http.HttpStatus;

/**
 * Marks a method or exception class with the status {@link #code} and
 * {@link #reason} that should be returned.
 *
 * <p>The status code is applied to the HTTP response when the handler
 * method is invoked and overrides status information set by other means,
 * like {@code ResponseEntity} or {@code "redirect:"}.
 *
 * <p><strong>Warning</strong>: when using this annotation on an exception
 * class, or when setting the {@code reason} attribute of this annotation,
 * the {@code HttpServletResponse.sendError} method will be used.
 *
 * <p>With {@code HttpServletResponse.sendError}, the response is considered
 * complete and should not be written to any further. Furthermore, the Servlet
 * container will typically write an HTML error page therefore making the
 * use of a {@code reason} unsuitable for REST APIs. For such cases it is
 * preferable to use a {@link com.rocket.summer.framework.http.ResponseEntity} as
 * a return type and avoid the use of {@code @ResponseStatus} altogether.
 *
 * <p>Note that a controller class may also be annotated with
 * {@code @ResponseStatus} and is then inherited by all {@code @RequestMapping}
 * methods.
 *
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 3.0
 * @see com.rocket.summer.framework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStatus {

    /**
     * Alias for {@link #code}.
     */
    @AliasFor("code")
    HttpStatus value() default HttpStatus.INTERNAL_SERVER_ERROR;

    /**
     * The status <em>code</em> to use for the response.
     * <p>Default is {@link HttpStatus#INTERNAL_SERVER_ERROR}, which should
     * typically be changed to something more appropriate.
     * @since 4.2
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    @AliasFor("value")
    HttpStatus code() default HttpStatus.INTERNAL_SERVER_ERROR;

    /**
     * The <em>reason</em> to be used for the response.
     * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
     */
    String reason() default "";

}
