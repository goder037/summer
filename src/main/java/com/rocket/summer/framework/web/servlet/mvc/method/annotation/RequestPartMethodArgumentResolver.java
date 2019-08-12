package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.GenericCollectionTypeResolver;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.validation.BindingResult;
import com.rocket.summer.framework.web.bind.MethodArgumentNotValidException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.RequestBody;
import com.rocket.summer.framework.web.bind.annotation.RequestParam;
import com.rocket.summer.framework.web.bind.annotation.RequestPart;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.multipart.*;
import com.rocket.summer.framework.web.multipart.support.MissingServletRequestPartException;
import com.rocket.summer.framework.web.multipart.support.RequestPartServletServerHttpRequest;
import com.rocket.summer.framework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import com.rocket.summer.framework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * Resolves the following method arguments:
 * <ul>
 * 	<li>Annotated with {@code @RequestPart}
 * 	<li>Of type {@link MultipartFile} in conjunction with Spring's
 *  {@link MultipartResolver} abstraction
 * 	<li>Of type {@code javax.servlet.http.Part} in conjunction with
 * 	Servlet 3.0 multipart requests
 * </ul>
 *
 * <p>When a parameter is annotated with {@code @RequestPart} the content of the
 * part is passed through an {@link HttpMessageConverter} to resolve the method
 * argument with the 'Content-Type' of the request part in mind. This is
 * analogous to what @{@link RequestBody} does to resolve an argument based on
 * the content of a regular request.
 *
 * <p>When a parameter is not annotated or the name of the part is not specified,
 * it is derived from the name of the method argument.
 *
 * <p>Automatic validation may be applied if the argument is annotated with
 * {@code @javax.validation.Valid}. In case of validation failure, a
 * {@link MethodArgumentNotValidException} is raised and a 400 response status
 * code returned if {@link DefaultHandlerExceptionResolver} is configured.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class RequestPartMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

    public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    /**
     * Supports the following:
     * <ul>
     * 	<li>Annotated with {@code @RequestPart}
     * 	<li>Of type {@link MultipartFile} unless annotated with {@code @RequestParam}.
     * 	<li>Of type {@code javax.servlet.http.Part} unless annotated with {@code @RequestParam}.
     * </ul>
     */
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestPart.class)) {
            return true;
        }
        else {
            if (parameter.hasParameterAnnotation(RequestParam.class)){
                return false;
            }
            else if (MultipartFile.class.equals(parameter.getParameterType())) {
                return true;
            }
            else if ("javax.servlet.http.Part".equals(parameter.getParameterType().getName())) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        assertIsMultipartRequest(servletRequest);

        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(servletRequest, MultipartHttpServletRequest.class);

        String partName = getPartName(parameter);
        Object arg;

        if (MultipartFile.class.equals(parameter.getParameterType())) {
            Assert.notNull(multipartRequest, "Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
            arg = multipartRequest.getFile(partName);
        }
        else if (isMultipartFileCollection(parameter)) {
            Assert.notNull(multipartRequest, "Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
            arg = multipartRequest.getFiles(partName);
        }
        else if ("javax.servlet.http.Part".equals(parameter.getParameterType().getName())) {
            arg = servletRequest.getPart(partName);
        }
        else {
            try {
                HttpInputMessage inputMessage = new RequestPartServletServerHttpRequest(servletRequest, partName);
                arg = readWithMessageConverters(inputMessage, parameter, parameter.getParameterType());
                if (arg != null) {
                    Annotation[] annotations = parameter.getParameterAnnotations();
                    for (Annotation annot : annotations) {
                        if (annot.annotationType().getSimpleName().startsWith("Valid")) {
                            WebDataBinder binder = binderFactory.createBinder(request, arg, partName);
                            Object hints = AnnotationUtils.getValue(annot);
                            binder.validate(hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
                            BindingResult bindingResult = binder.getBindingResult();
                            if (bindingResult.hasErrors()) {
                                throw new MethodArgumentNotValidException(parameter, bindingResult);
                            }
                        }
                    }
                }
            }
            catch (MissingServletRequestPartException ex) {
                // handled below
                arg = null;
            }
        }

        RequestPart annot = parameter.getParameterAnnotation(RequestPart.class);
        boolean isRequired = (annot == null || annot.required());

        if (arg == null && isRequired) {
            throw new MissingServletRequestPartException(partName);
        }

        return arg;
    }

    private static void assertIsMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            throw new MultipartException("The current request is not a multipart request");
        }
    }

    private String getPartName(MethodParameter parameter) {
        RequestPart annot = parameter.getParameterAnnotation(RequestPart.class);
        String partName = (annot != null) ? annot.value() : "";
        if (partName.length() == 0) {
            partName = parameter.getParameterName();
            Assert.notNull(partName, "Request part name for argument type [" + parameter.getParameterType().getName()
                    + "] not available, and parameter name information not found in class file either.");
        }
        return partName;
    }

    private boolean isMultipartFileCollection(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        if (Collection.class.equals(paramType) || List.class.isAssignableFrom(paramType)){
            Class<?> valueType = GenericCollectionTypeResolver.getCollectionParameterType(parameter);
            if (valueType != null && valueType.equals(MultipartFile.class)) {
                return true;
            }
        }
        return false;
    }

}
