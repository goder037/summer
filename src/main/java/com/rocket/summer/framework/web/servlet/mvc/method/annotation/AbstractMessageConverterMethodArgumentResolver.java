package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.HttpMethod;
import com.rocket.summer.framework.http.HttpRequest;
import com.rocket.summer.framework.http.InvalidMediaTypeException;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.GenericHttpMessageConverter;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.converter.HttpMessageNotReadableException;
import com.rocket.summer.framework.http.server.ServletServerHttpRequest;
import com.rocket.summer.framework.lang.UsesJava8;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.validation.Errors;
import com.rocket.summer.framework.validation.annotation.Validated;
import com.rocket.summer.framework.web.HttpMediaTypeNotSupportedException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;

/**
 * A base class for resolving method argument values by reading from the body of
 * a request with {@link HttpMessageConverter}s.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public abstract class AbstractMessageConverterMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Set<HttpMethod> SUPPORTED_METHODS =
            EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);

    private static final Object NO_VALUE = new Object();


    protected final Log logger = LogFactory.getLog(getClass());

    protected final List<HttpMessageConverter<?>> messageConverters;

    protected final List<MediaType> allSupportedMediaTypes;

    private final RequestResponseBodyAdviceChain advice;


    /**
     * Basic constructor with converters only.
     */
    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters) {
        this(converters, null);
    }

    /**
     * Constructor with converters and {@code Request~} and {@code ResponseBodyAdvice}.
     * @since 4.2
     */
    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters,
                                                          List<Object> requestResponseBodyAdvice) {

        Assert.notEmpty(converters, "'messageConverters' must not be empty");
        this.messageConverters = converters;
        this.allSupportedMediaTypes = getAllSupportedMediaTypes(converters);
        this.advice = new RequestResponseBodyAdviceChain(requestResponseBodyAdvice);
    }


    /**
     * Return the media types supported by all provided message converters sorted
     * by specificity via {@link MediaType#sortBySpecificity(List)}.
     */
    private static List<MediaType> getAllSupportedMediaTypes(List<HttpMessageConverter<?>> messageConverters) {
        Set<MediaType> allSupportedMediaTypes = new LinkedHashSet<MediaType>();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
        }
        List<MediaType> result = new ArrayList<MediaType>(allSupportedMediaTypes);
        MediaType.sortBySpecificity(result);
        return Collections.unmodifiableList(result);
    }


    /**
     * Return the configured {@link RequestBodyAdvice} and
     * {@link RequestBodyAdvice} where each instance may be wrapped as a
     * {@link com.rocket.summer.framework.web.method.ControllerAdviceBean ControllerAdviceBean}.
     */
    protected RequestResponseBodyAdviceChain getAdvice() {
        return this.advice;
    }

    /**
     * Create the method argument value of the expected parameter type by
     * reading from the given request.
     * @param <T> the expected type of the argument value to be created
     * @param webRequest the current request
     * @param parameter the method parameter descriptor (may be {@code null})
     * @param paramType the type of the argument value to be created
     * @return the created method argument value
     * @throws IOException if the reading from the request fails
     * @throws HttpMediaTypeNotSupportedException if no suitable message converter is found
     */
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter,
                                                   Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {

        HttpInputMessage inputMessage = createInputMessage(webRequest);
        return readWithMessageConverters(inputMessage, parameter, paramType);
    }

    /**
     * Create the method argument value of the expected parameter type by reading
     * from the given HttpInputMessage.
     * @param <T> the expected type of the argument value to be created
     * @param inputMessage the HTTP input message representing the current request
     * @param parameter the method parameter descriptor (may be {@code null})
     * @param targetType the target type, not necessarily the same as the method
     * parameter type, e.g. for {@code HttpEntity<String>}.
     * @return the created method argument value
     * @throws IOException if the reading from the request fails
     * @throws HttpMediaTypeNotSupportedException if no suitable message converter is found
     */
    @SuppressWarnings("unchecked")
    protected <T> Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter parameter,
                                                   Type targetType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {

        MediaType contentType;
        boolean noContentType = false;
        try {
            contentType = inputMessage.getHeaders().getContentType();
        }
        catch (InvalidMediaTypeException ex) {
            throw new HttpMediaTypeNotSupportedException(ex.getMessage());
        }
        if (contentType == null) {
            noContentType = true;
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        Class<?> contextClass = (parameter != null ? parameter.getContainingClass() : null);
        Class<T> targetClass = (targetType instanceof Class ? (Class<T>) targetType : null);
        if (targetClass == null) {
            ResolvableType resolvableType = (parameter != null ?
                    ResolvableType.forMethodParameter(parameter) : ResolvableType.forType(targetType));
            targetClass = (Class<T>) resolvableType.resolve();
        }

        HttpMethod httpMethod = ((HttpRequest) inputMessage).getMethod();
        Object body = NO_VALUE;

        try {
            inputMessage = new EmptyBodyCheckingHttpInputMessage(inputMessage);

            for (HttpMessageConverter<?> converter : this.messageConverters) {
                Class<HttpMessageConverter<?>> converterType = (Class<HttpMessageConverter<?>>) converter.getClass();
                if (converter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter<?>) converter;
                    if (genericConverter.canRead(targetType, contextClass, contentType)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Read [" + targetType + "] as \"" + contentType + "\" with [" + converter + "]");
                        }
                        if (inputMessage.getBody() != null) {
                            inputMessage = getAdvice().beforeBodyRead(inputMessage, parameter, targetType, converterType);
                            body = genericConverter.read(targetType, contextClass, inputMessage);
                            body = getAdvice().afterBodyRead(body, inputMessage, parameter, targetType, converterType);
                        }
                        else {
                            body = getAdvice().handleEmptyBody(null, inputMessage, parameter, targetType, converterType);
                        }
                        break;
                    }
                }
                else if (targetClass != null) {
                    if (converter.canRead(targetClass, contentType)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Read [" + targetType + "] as \"" + contentType + "\" with [" + converter + "]");
                        }
                        if (inputMessage.getBody() != null) {
                            inputMessage = getAdvice().beforeBodyRead(inputMessage, parameter, targetType, converterType);
                            body = ((HttpMessageConverter<T>) converter).read(targetClass, inputMessage);
                            body = getAdvice().afterBodyRead(body, inputMessage, parameter, targetType, converterType);
                        }
                        else {
                            body = getAdvice().handleEmptyBody(null, inputMessage, parameter, targetType, converterType);
                        }
                        break;
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", ex);
        }

        if (body == NO_VALUE) {
            if (httpMethod == null || !SUPPORTED_METHODS.contains(httpMethod) ||
                    (noContentType && inputMessage.getBody() == null)) {
                return null;
            }
            throw new HttpMediaTypeNotSupportedException(contentType, this.allSupportedMediaTypes);
        }

        return body;
    }

    /**
     * Create a new {@link HttpInputMessage} from the given {@link NativeWebRequest}.
     * @param webRequest the web request to create an input message from
     * @return the input message
     */
    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    /**
     * Validate the binding target if applicable.
     * <p>The default implementation checks for {@code @javax.validation.Valid},
     * Spring's {@link com.rocket.summer.framework.validation.annotation.Validated},
     * and custom annotations whose name starts with "Valid".
     * @param binder the DataBinder to be used
     * @param parameter the method parameter descriptor
     * @since 4.1.5
     * @see #isBindExceptionRequired
     */
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
            if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
                Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
                binder.validate(validationHints);
                break;
            }
        }
    }

    /**
     * Whether to raise a fatal bind exception on validation errors.
     * @param binder the data binder used to perform data binding
     * @param parameter the method parameter descriptor
     * @return {@code true} if the next method argument is not of type {@link Errors}
     * @since 4.1.5
     */
    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getMethod().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));
        return !hasBindingResult;
    }

    /**
     * Adapt the given argument against the method parameter, if necessary.
     * @param arg the resolved argument
     * @param parameter the method parameter descriptor
     * @return the adapted argument, or the original resolved argument as-is
     * @since 4.3.5
     */
    protected Object adaptArgumentIfNecessary(Object arg, MethodParameter parameter) {
        return (parameter.isOptional() ? OptionalResolver.resolveValue(arg) : arg);
    }


    private static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders headers;

        private final InputStream body;

        private final HttpMethod method;


        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream == null) {
                this.body = null;
            }
            else if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = (inputStream.read() != -1 ? inputStream : null);
                inputStream.reset();
            }
            else {
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
                int b = pushbackInputStream.read();
                if (b == -1) {
                    this.body = null;
                }
                else {
                    this.body = pushbackInputStream;
                    pushbackInputStream.unread(b);
                }
            }
            this.method = ((HttpRequest) inputMessage).getMethod();
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            return this.body;
        }

        public HttpMethod getMethod() {
            return this.method;
        }
    }


    /**
     * Inner class to avoid hard-coded dependency on Java 8 Optional type...
     */
    @UsesJava8
    private static class OptionalResolver {

        public static Object resolveValue(Object value) {
            if (value == null || (value instanceof Collection && ((Collection) value).isEmpty()) ||
                    (value instanceof Object[] && ((Object[]) value).length == 0)) {
                return Optional.empty();
            }
            return Optional.of(value);
        }
    }

}
