package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.server.ServerHttpRequest;
import com.rocket.summer.framework.http.server.ServerHttpResponse;

/**
 * Allows customizing the response after the execution of an {@code @ResponseBody}
 * or a {@code ResponseEntity} controller method but before the body is written
 * with an {@code HttpMessageConverter}.
 *
 * <p>Implementations may be registered directly with
 * {@code RequestMappingHandlerAdapter} and {@code ExceptionHandlerExceptionResolver}
 * or more likely annotated with {@code @ControllerAdvice} in which case they
 * will be auto-detected by both.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public interface ResponseBodyAdvice<T> {

    /**
     * Whether this component supports the given controller method return type
     * and the selected {@code HttpMessageConverter} type.
     * @param returnType the return type
     * @param converterType the selected converter type
     * @return {@code true} if {@link #beforeBodyWrite} should be invoked;
     * {@code false} otherwise
     */
    boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType);

    /**
     * Invoked after an {@code HttpMessageConverter} is selected and just before
     * its write method is invoked.
     * @param body the body to be written
     * @param returnType the return type of the controller method
     * @param selectedContentType the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request the current request
     * @param response the current response
     * @return the body that was passed in or a modified (possibly new) instance
     */
    T beforeBodyWrite(T body, MethodParameter returnType, MediaType selectedContentType,
                      Class<? extends HttpMessageConverter<?>> selectedConverterType,
                      ServerHttpRequest request, ServerHttpResponse response);

}

