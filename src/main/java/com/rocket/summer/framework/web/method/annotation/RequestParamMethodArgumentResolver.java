package com.rocket.summer.framework.web.method.annotation;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.MissingServletRequestParameterException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.RequestParam;
import com.rocket.summer.framework.web.bind.annotation.RequestPart;
import com.rocket.summer.framework.web.bind.annotation.ValueConstants;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.UriComponentsContributor;
import com.rocket.summer.framework.web.multipart.MultipartException;
import com.rocket.summer.framework.web.multipart.MultipartFile;
import com.rocket.summer.framework.web.multipart.MultipartHttpServletRequest;
import com.rocket.summer.framework.web.multipart.MultipartResolver;
import com.rocket.summer.framework.web.multipart.support.MissingServletRequestPartException;
import com.rocket.summer.framework.web.multipart.support.MultipartResolutionDelegate;
import com.rocket.summer.framework.web.util.UriComponentsBuilder;
import com.rocket.summer.framework.web.util.WebUtils;

/**
 * Resolves method arguments annotated with @{@link RequestParam}, arguments of
 * type {@link MultipartFile} in conjunction with Spring's {@link MultipartResolver}
 * abstraction, and arguments of type {@code javax.servlet.http.Part} in conjunction
 * with Servlet 3.0 multipart requests. This resolver can also be created in default
 * resolution mode in which simple types (int, long, etc.) not annotated with
 * {@link RequestParam @RequestParam} are also treated as request parameters with
 * the parameter name derived from the argument name.
 *
 * <p>If the method parameter type is {@link Map}, the name specified in the
 * annotation is used to resolve the request parameter String value. The value is
 * then converted to a {@link Map} via type conversion assuming a suitable
 * {@link Converter} or {@link PropertyEditor} has been registered.
 * Or if a request parameter name is not specified the
 * {@link RequestParamMapMethodArgumentResolver} is used instead to provide
 * access to all request parameters in the form of a map.
 *
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved request
 * header values that don't yet match the method parameter type.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 3.1
 * @see RequestParamMapMethodArgumentResolver
 */
public class RequestParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
        implements UriComponentsContributor {

    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    private final boolean useDefaultResolution;


    /**
     * Create a new {@link RequestParamMethodArgumentResolver} instance.
     * @param useDefaultResolution in default resolution mode a method argument
     * that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
     * is treated as a request parameter even if it isn't annotated, the
     * request parameter name is derived from the method parameter name.
     */
    public RequestParamMethodArgumentResolver(boolean useDefaultResolution) {
        this.useDefaultResolution = useDefaultResolution;
    }

    /**
     * Create a new {@link RequestParamMethodArgumentResolver} instance.
     * @param beanFactory a bean factory used for resolving  ${...} placeholder
     * and #{...} SpEL expressions in default values, or {@code null} if default
     * values are not expected to contain expressions
     * @param useDefaultResolution in default resolution mode a method argument
     * that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
     * is treated as a request parameter even if it isn't annotated, the
     * request parameter name is derived from the method parameter name.
     */
    public RequestParamMethodArgumentResolver(ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
        super(beanFactory);
        this.useDefaultResolution = useDefaultResolution;
    }


    /**
     * Supports the following:
     * <ul>
     * <li>@RequestParam-annotated method arguments.
     * This excludes {@link Map} params where the annotation does not specify a name.
     * See {@link RequestParamMapMethodArgumentResolver} instead for such params.
     * <li>Arguments of type {@link MultipartFile} unless annotated with @{@link RequestPart}.
     * <li>Arguments of type {@code Part} unless annotated with @{@link RequestPart}.
     * <li>In default resolution mode, simple type arguments even if not with @{@link RequestParam}.
     * </ul>
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestParam.class)) {
            if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
                String paramName = parameter.getParameterAnnotation(RequestParam.class).name();
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
            parameter = parameter.nestedIfOptional();
            if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
                return true;
            }
            else if (this.useDefaultResolution) {
                return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
            }
            else {
                return false;
            }
        }
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestParam ann = parameter.getParameterAnnotation(RequestParam.class);
        return (ann != null ? new RequestParamNamedValueInfo(ann) : new RequestParamNamedValueInfo());
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(servletRequest, MultipartHttpServletRequest.class);

        Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
        if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
            return mpArg;
        }

        Object arg = null;
        if (multipartRequest != null) {
            List<MultipartFile> files = multipartRequest.getFiles(name);
            if (!files.isEmpty()) {
                arg = (files.size() == 1 ? files.get(0) : files);
            }
        }
        if (arg == null) {
            String[] paramValues = request.getParameterValues(name);
            if (paramValues != null) {
                arg = (paramValues.length == 1 ? paramValues[0] : paramValues);
            }
        }
        return arg;
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request)
            throws Exception {

        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
            if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            }
            else {
                throw new MissingServletRequestPartException(name);
            }
        }
        else {
            throw new MissingServletRequestParameterException(name,
                    parameter.getNestedParameterType().getSimpleName());
        }
    }

    @Override
    public void contributeMethodArgument(MethodParameter parameter, Object value,
                                         UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {

        Class<?> paramType = parameter.getNestedParameterType();
        if (Map.class.isAssignableFrom(paramType) || MultipartFile.class == paramType ||
                "javax.servlet.http.Part".equals(paramType.getName())) {
            return;
        }

        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        String name = (requestParam == null || StringUtils.isEmpty(requestParam.name()) ?
                parameter.getParameterName() : requestParam.name());

        if (value == null) {
            if (requestParam != null) {
                if (!requestParam.required() || !requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    return;
                }
            }
            builder.queryParam(name);
        }
        else if (value instanceof Collection) {
            for (Object element : (Collection<?>) value) {
                element = formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element);
                builder.queryParam(name, element);
            }
        }
        else {
            builder.queryParam(name, formatUriValue(conversionService, new TypeDescriptor(parameter), value));
        }
    }

    protected String formatUriValue(ConversionService cs, TypeDescriptor sourceType, Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof String) {
            return (String) value;
        }
        else if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        else {
            return value.toString();
        }
    }


    private static class RequestParamNamedValueInfo extends NamedValueInfo {

        public RequestParamNamedValueInfo() {
            super("", false, ValueConstants.DEFAULT_NONE);
        }

        public RequestParamNamedValueInfo(RequestParam annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }

}
