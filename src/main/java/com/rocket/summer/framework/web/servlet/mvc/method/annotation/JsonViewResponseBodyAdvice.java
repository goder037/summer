package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.fasterxml.jackson.annotation.JsonView;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.converter.json.MappingJacksonValue;
import com.rocket.summer.framework.http.server.ServerHttpRequest;
import com.rocket.summer.framework.http.server.ServerHttpResponse;

/**
 * A {@link ResponseBodyAdvice} implementation that adds support for Jackson's
 * {@code @JsonView} annotation declared on a Spring MVC {@code @RequestMapping}
 * or {@code @ExceptionHandler} method.
 *
 * <p>The serialization view specified in the annotation will be passed in to the
 * {@link com.rocket.summer.framework.http.converter.json.MappingJackson2HttpMessageConverter}
 * which will then use it to serialize the response body.
 *
 * <p>Note that despite {@code @JsonView} allowing for more than one class to
 * be specified, the use for a response body advice is only supported with
 * exactly one class argument. Consider the use of a composite interface.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see com.fasterxml.jackson.annotation.JsonView
 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
 */
public class JsonViewResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return super.supports(returnType, converterType) && returnType.hasMethodAnnotation(JsonView.class);
    }

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
                                           MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {

        JsonView annotation = returnType.getMethodAnnotation(JsonView.class);
        Class<?>[] classes = annotation.value();
        if (classes.length != 1) {
            throw new IllegalArgumentException(
                    "@JsonView only supported for response body advice with exactly 1 class argument: " + returnType);
        }
        bodyContainer.setSerializationView(classes[0]);
    }

}
