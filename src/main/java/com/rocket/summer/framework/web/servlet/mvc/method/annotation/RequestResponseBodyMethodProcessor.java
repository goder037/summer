package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.core.Conventions;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.converter.HttpMessageNotReadableException;
import com.rocket.summer.framework.http.converter.HttpMessageNotWritableException;
import com.rocket.summer.framework.http.server.ServletServerHttpRequest;
import com.rocket.summer.framework.http.server.ServletServerHttpResponse;
import com.rocket.summer.framework.validation.BindingResult;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.HttpMediaTypeNotSupportedException;
import com.rocket.summer.framework.web.accept.ContentNegotiationManager;
import com.rocket.summer.framework.web.bind.MethodArgumentNotValidException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.RequestBody;
import com.rocket.summer.framework.web.bind.annotation.ResponseBody;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * Resolves method arguments annotated with {@code @RequestBody} and handles return
 * values from methods annotated with {@code @ResponseBody} by reading and writing
 * to the body of the request or response with an {@link HttpMessageConverter}.
 *
 * <p>An {@code @RequestBody} method argument is also validated if it is annotated
 * with {@code @javax.validation.Valid}. In case of validation failure,
 * {@link MethodArgumentNotValidException} is raised and results in an HTTP 400
 * response status code if {@link DefaultHandlerExceptionResolver} is configured.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {

    /**
     * Basic constructor with converters only. Suitable for resolving
     * {@code @RequestBody}. For handling {@code @ResponseBody} consider also
     * providing a {@code ContentNegotiationManager}.
     */
    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    /**
     * Basic constructor with converters and {@code ContentNegotiationManager}.
     * Suitable for resolving {@code @RequestBody} and handling
     * {@code @ResponseBody} without {@code Request~} or
     * {@code ResponseBodyAdvice}.
     */
    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters,
                                              ContentNegotiationManager manager) {

        super(converters, manager);
    }

    /**
     * Complete constructor for resolving {@code @RequestBody} method arguments.
     * For handling {@code @ResponseBody} consider also providing a
     * {@code ContentNegotiationManager}.
     * @since 4.2
     */
    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters,
                                              List<Object> requestResponseBodyAdvice) {

        super(converters, null, requestResponseBodyAdvice);
    }

    /**
     * Complete constructor for resolving {@code @RequestBody} and handling
     * {@code @ResponseBody}.
     */
    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters,
                                              ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {

        super(converters, manager, requestResponseBodyAdvice);
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) ||
                returnType.hasMethodAnnotation(ResponseBody.class));
    }

    /**
     * Throws MethodArgumentNotValidException if validation fails.
     * @throws HttpMessageNotReadableException if {@link RequestBody#required()}
     * is {@code true} and there is no body content or if there is no suitable
     * converter to read the content with.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        parameter = parameter.nestedIfOptional();
        Object arg = readWithMessageConverters(webRequest, parameter, parameter.getNestedGenericParameterType());
        String name = Conventions.getVariableNameForParameter(parameter);

        WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
        if (arg != null) {
            validateIfApplicable(binder, parameter);
            if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
            }
        }
        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());

        return adaptArgumentIfNecessary(arg, parameter);
    }

    @Override
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter,
                                                   Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);

        Object arg = readWithMessageConverters(inputMessage, parameter, paramType);
        if (arg == null) {
            if (checkRequired(parameter)) {
                throw new HttpMessageNotReadableException("Required request body is missing: " +
                        parameter.getMethod().toGenericString());
            }
        }
        return arg;
    }

    protected boolean checkRequired(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(RequestBody.class).required() && !parameter.isOptional());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

        mavContainer.setRequestHandled(true);
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

        // Try even with null return value. ResponseBodyAdvice could get involved.
        writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
    }

}
