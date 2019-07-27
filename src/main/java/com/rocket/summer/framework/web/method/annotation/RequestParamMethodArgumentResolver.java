package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.GenericCollectionTypeResolver;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.MissingServletRequestParameterException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.RequestParam;
import com.rocket.summer.framework.web.bind.annotation.RequestPart;
import com.rocket.summer.framework.web.bind.annotation.ValueConstants;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.multipart.MultipartException;
import com.rocket.summer.framework.web.multipart.MultipartFile;
import com.rocket.summer.framework.web.multipart.MultipartHttpServletRequest;
import com.rocket.summer.framework.web.multipart.MultipartResolver;
import com.rocket.summer.framework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Resolves method arguments annotated with @{@link RequestParam}, arguments of
 * type {@link MultipartFile} in conjunction with Spring's {@link MultipartResolver}
 * abstraction, and arguments of type {@code javax.servlet.http.Part} in conjunction
 * with Servlet 3.0 multipart requests. This resolver can also be created in default
 * resolution mode in which simple types (int, long, etc.) not annotated
 * with @{@link RequestParam} are also treated as request parameters with the
 * parameter name derived from the argument name.
 *
 * <p>If the method parameter type is {@link Map}, the request parameter name is used to
 * resolve the request parameter String value. The value is then converted to a {@link Map}
 * via type conversion assuming a suitable {@link Converter} or {@link PropertyEditor} has
 * been registered. If a request parameter name is not specified with a {@link Map} method
 * parameter type, the {@link RequestParamMapMethodArgumentResolver} is used instead
 * providing access to all request parameters in the form of a map.
 *
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved request
 * header values that don't yet match the method parameter type.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see RequestParamMapMethodArgumentResolver
 */
public class RequestParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    private final boolean useDefaultResolution;

    /**
     * @param beanFactory a bean factory used for resolving  ${...} placeholder
     * and #{...} SpEL expressions in default values, or {@code null} if default
     * values are not expected to contain expressions
     * @param useDefaultResolution in default resolution mode a method argument
     * that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
     * is treated as a request parameter even if it itsn't annotated, the
     * request parameter name is derived from the method parameter name.
     */
    public RequestParamMethodArgumentResolver(ConfigurableBeanFactory beanFactory,
                                              boolean useDefaultResolution) {
        super(beanFactory);
        this.useDefaultResolution = useDefaultResolution;
    }

    /**
     * Supports the following:
     * <ul>
     * 	<li>@RequestParam-annotated method arguments.
     * 		This excludes {@link Map} params where the annotation doesn't
     * 		specify a name.	See {@link RequestParamMapMethodArgumentResolver}
     * 		instead for such params.
     * 	<li>Arguments of type {@link MultipartFile}
     * 		unless annotated with @{@link RequestPart}.
     * 	<li>Arguments of type {@code javax.servlet.http.Part}
     * 		unless annotated with @{@link RequestPart}.
     * 	<li>In default resolution mode, simple type arguments
     * 		even if not with @{@link RequestParam}.
     * </ul>
     */
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        if (parameter.hasParameterAnnotation(RequestParam.class)) {
            if (Map.class.isAssignableFrom(paramType)) {
                String paramName = parameter.getParameterAnnotation(RequestParam.class).value();
                return StringUtils.hasText(paramName);
            }
            else {
                return true;
            }
        }
        else {
            if (parameter.hasParameterAnnotation(RequestPart.class)) {
                return false;
            }
            else if (MultipartFile.class.equals(paramType) || "javax.servlet.http.Part".equals(paramType.getName())) {
                return true;
            }
            else if (this.useDefaultResolution) {
                return BeanUtils.isSimpleProperty(paramType);
            }
            else {
                return false;
            }
        }
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestParam annotation = parameter.getParameterAnnotation(RequestParam.class);
        return (annotation != null) ?
                new RequestParamNamedValueInfo(annotation) :
                new RequestParamNamedValueInfo();
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {

        Object arg;

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(servletRequest, MultipartHttpServletRequest.class);

        if (MultipartFile.class.equals(parameter.getParameterType())) {
            assertIsMultipartRequest(servletRequest);
            Assert.notNull(multipartRequest, "Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
            arg = multipartRequest.getFile(name);
        }
        else if (isMultipartFileCollection(parameter)) {
            assertIsMultipartRequest(servletRequest);
            Assert.notNull(multipartRequest, "Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
            arg = multipartRequest.getFiles(name);
        }
        else if ("javax.servlet.http.Part".equals(parameter.getParameterType().getName())) {
            assertIsMultipartRequest(servletRequest);
            arg = servletRequest.getPart(name);
        }
        else {
            arg = null;
            if (multipartRequest != null) {
                List<MultipartFile> files = multipartRequest.getFiles(name);
                if (!files.isEmpty()) {
                    arg = (files.size() == 1 ? files.get(0) : files);
                }
            }
            if (arg == null) {
                String[] paramValues = webRequest.getParameterValues(name);
                if (paramValues != null) {
                    arg = paramValues.length == 1 ? paramValues[0] : paramValues;
                }
            }
        }

        return arg;
    }

    private void assertIsMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            throw new MultipartException("The current request is not a multipart request");
        }
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

    @Override
    protected void handleMissingValue(String paramName, MethodParameter parameter) throws ServletException {
        throw new MissingServletRequestParameterException(paramName, parameter.getParameterType().getSimpleName());
    }

    private class RequestParamNamedValueInfo extends NamedValueInfo {

        private RequestParamNamedValueInfo() {
            super("", false, ValueConstants.DEFAULT_NONE);
        }

        private RequestParamNamedValueInfo(RequestParam annotation) {
            super(annotation.value(), annotation.required(), annotation.defaultValue());
        }
    }
}
