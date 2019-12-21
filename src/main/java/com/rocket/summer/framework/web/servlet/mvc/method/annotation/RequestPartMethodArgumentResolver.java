package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.validation.BindingResult;
import com.rocket.summer.framework.web.bind.MethodArgumentNotValidException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.RequestBody;
import com.rocket.summer.framework.web.bind.annotation.RequestParam;
import com.rocket.summer.framework.web.bind.annotation.RequestPart;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.multipart.MultipartException;
import com.rocket.summer.framework.web.multipart.MultipartFile;
import com.rocket.summer.framework.web.multipart.MultipartResolver;
import com.rocket.summer.framework.web.multipart.support.MissingServletRequestPartException;
import com.rocket.summer.framework.web.multipart.support.MultipartResolutionDelegate;
import com.rocket.summer.framework.web.multipart.support.RequestPartServletServerHttpRequest;

/**
 * Resolves the following method arguments:
 * <ul>
 * <li>Annotated with @{@link RequestPart}
 * <li>Of type {@link MultipartFile} in conjunction with Spring's {@link MultipartResolver} abstraction
 * <li>Of type {@code javax.servlet.http.Part} in conjunction with Servlet 3.0 multipart requests
 * </ul>
 *
 * <p>When a parameter is annotated with {@code @RequestPart}, the content of the part is
 * passed through an {@link HttpMessageConverter} to resolve the method argument with the
 * 'Content-Type' of the request part in mind. This is analogous to what @{@link RequestBody}
 * does to resolve an argument based on the content of a regular request.
 *
 * <p>When a parameter is not annotated or the name of the part is not specified,
 * it is derived from the name of the method argument.
 *
 * <p>Automatic validation may be applied if the argument is annotated with
 * {@code @javax.validation.Valid}. In case of validation failure, a {@link MethodArgumentNotValidException}
 * is raised and a 400 response status code returned if
 * {@link com.rocket.summer.framework.web.servlet.mvc.support.DefaultHandlerExceptionResolver} is configured.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Juergen Hoeller
 * @since 3.1
 */
public class RequestPartMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

    /**
     * Basic constructor with converters only.
     */
    public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    /**
     * Constructor with converters and {@code Request~} and
     * {@code ResponseBodyAdvice}.
     */
    public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters,
                                             List<Object> requestResponseBodyAdvice) {

        super(messageConverters, requestResponseBodyAdvice);
    }


    /**
     * Whether the given {@linkplain MethodParameter method parameter} is a multi-part
     * supported. Supports the following:
     * <ul>
     * <li>annotated with {@code @RequestPart}
     * <li>of type {@link MultipartFile} unless annotated with {@code @RequestParam}
     * <li>of type {@code javax.servlet.http.Part} unless annotated with
     * {@code @RequestParam}
     * </ul>
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestPart.class)) {
            return true;
        }
        else {
            if (parameter.hasParameterAnnotation(RequestParam.class)) {
                return false;
            }
            return MultipartResolutionDelegate.isMultipartArgument(parameter.nestedIfOptional());
        }
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        RequestPart requestPart = parameter.getParameterAnnotation(RequestPart.class);
        boolean isRequired = ((requestPart == null || requestPart.required()) && !parameter.isOptional());

        String name = getPartName(parameter, requestPart);
        parameter = parameter.nestedIfOptional();
        Object arg = null;

        Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
        if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
            arg = mpArg;
        }
        else {
            try {
                HttpInputMessage inputMessage = new RequestPartServletServerHttpRequest(servletRequest, name);
                arg = readWithMessageConverters(inputMessage, parameter, parameter.getNestedGenericParameterType());
                WebDataBinder binder = binderFactory.createBinder(request, arg, name);
                if (arg != null) {
                    validateIfApplicable(binder, parameter);
                    if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                        throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                    }
                }
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
            }
            catch (MissingServletRequestPartException ex) {
                if (isRequired) {
                    throw ex;
                }
            }
            catch (MultipartException ex) {
                if (isRequired) {
                    throw ex;
                }
            }
        }

        if (arg == null && isRequired) {
            if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            }
            else {
                throw new MissingServletRequestPartException(name);
            }
        }
        return adaptArgumentIfNecessary(arg, parameter);
    }

    private String getPartName(MethodParameter methodParam, RequestPart requestPart) {
        String partName = (requestPart != null ? requestPart.name() : "");
        if (partName.isEmpty()) {
            partName = methodParam.getParameterName();
            if (partName == null) {
                throw new IllegalArgumentException("Request part name for argument type [" +
                        methodParam.getNestedParameterType().getName() +
                        "] not specified, and parameter name information not found in class file either.");
            }
        }
        return partName;
    }

}
