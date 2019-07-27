package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.*;

/**
 * Annotation which indicates that a method return value should be bound to the web response body.
 * Supported for annotated handler methods in Servlet environments.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see RequestBody
 * @see com.rocket.summer.framework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {

}
