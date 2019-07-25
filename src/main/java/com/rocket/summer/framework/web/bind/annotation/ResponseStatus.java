package com.rocket.summer.framework.web.bind.annotation;

import com.rocket.summer.framework.http.HttpStatus;

import java.lang.annotation.*;

/**
 * Marks a method or exception class with the status code and reason that should be returned. The status code is applied
 * to the HTTP response when the handler method is invoked, or whenever said exception is thrown.
 *
 * @author Arjen Poutsma
 * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 * @since 3.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStatus {

    /**
     * The status code to use for the response.
     *
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    HttpStatus value();

    /**
     * The reason to be used for the response. <p>If this element is not set, it will default to the standard status
     * message for the status code.
     *
     * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
     */
    String reason() default "";

}

