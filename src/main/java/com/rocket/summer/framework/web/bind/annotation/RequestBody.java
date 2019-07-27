package com.rocket.summer.framework.web.bind.annotation;


import java.lang.annotation.*;

/**
 * Annotation indicating a method parameter should be bound to the body of the web request.
 * The body of the request is passed through an {@link HttpMessageConverter} to resolve the
 * method argument depending on the content type of the request. Optionally, automatic
 * validation can be applied by annotating the argument with {@code @Valid}.
 *
 * <p>Supported for annotated handler methods in Servlet environments.
 *
 * @author Arjen Poutsma
 * @see RequestHeader
 * @see ResponseBody
 * @see com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerMethodAdapter
 * @since 3.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {

}