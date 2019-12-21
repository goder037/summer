package com.rocket.summer.framework.web.bind.annotation;

import java.beans.PropertyEditor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.core.annotation.AliasFor;
import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.web.multipart.MultipartFile;
import com.rocket.summer.framework.web.multipart.MultipartResolver;

/**
 * Annotation that can be used to associate the part of a "multipart/form-data" request
 * with a method argument.
 *
 * <p>Supported method argument types include {@link MultipartFile} in conjunction with
 * Spring's {@link MultipartResolver} abstraction, {@code javax.servlet.http.Part} in
 * conjunction with Servlet 3.0 multipart requests, or otherwise for any other method
 * argument, the content of the part is passed through an {@link HttpMessageConverter}
 * taking into consideration the 'Content-Type' header of the request part. This is
 * analogous to what @{@link RequestBody} does to resolve an argument based on the
 * content of a non-multipart regular request.
 *
 * <p>Note that @{@link RequestParam} annotation can also be used to associate the part
 * of a "multipart/form-data" request with a method argument supporting the same method
 * argument types. The main difference is that when the method argument is not a String
 * or raw {@code MultipartFile} / {@code Part}, {@code @RequestParam} relies on type
 * conversion via a registered {@link Converter} or {@link PropertyEditor} while
 * {@link RequestPart} relies on {@link HttpMessageConverter HttpMessageConverters}
 * taking into consideration the 'Content-Type' header of the request part.
 * {@link RequestParam} is likely to be used with name-value form fields while
 * {@link RequestPart} is likely to be used with parts containing more complex content
 * e.g. JSON, XML).
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 3.1
 * @see RequestParam
 * @see com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPart {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the part in the {@code "multipart/form-data"} request to bind to.
     * @since 4.2
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether the part is required.
     * <p>Defaults to {@code true}, leading to an exception being thrown
     * if the part is missing in the request. Switch this to
     * {@code false} if you prefer a {@code null} value if the part is
     * not present in the request.
     */
    boolean required() default true;

}
