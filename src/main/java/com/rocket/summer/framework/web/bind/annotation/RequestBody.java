package com.rocket.summer.framework.web.bind.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.http.converter.HttpMessageConverter;

/**
 * Annotation indicating a method parameter should be bound to the body of the web request.
 * The body of the request is passed through an {@link HttpMessageConverter} to resolve the
 * method argument depending on the content type of the request. Optionally, automatic
 * validation can be applied by annotating the argument with {@code @Valid}.
 *
 * <p>Supported for annotated handler methods in Servlet environments.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see RequestHeader
 * @see ResponseBody
 * @see com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {

    /**
     * Whether body content is required.
     * <p>Default is {@code true}, leading to an exception thrown in case
     * there is no body content. Switch this to {@code false} if you prefer
     * {@code null} to be passed when the body content is {@code null}.
     * @since 3.2
     */
    boolean required() default true;

}