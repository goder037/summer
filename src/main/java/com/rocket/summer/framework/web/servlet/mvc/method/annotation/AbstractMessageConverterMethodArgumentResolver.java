package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.server.ServletServerHttpRequest;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.HttpMediaTypeNotSupportedException;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * A base class for resolving method argument values by reading from the body of a request
 * with {@link HttpMessageConverter}s.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractMessageConverterMethodArgumentResolver implements HandlerMethodArgumentResolver {

    protected final Log logger = LogFactory.getLog(getClass());

    protected final List<HttpMessageConverter<?>> messageConverters;

    protected final List<MediaType> allSupportedMediaTypes;

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.messageConverters = messageConverters;
        this.allSupportedMediaTypes = getAllSupportedMediaTypes(messageConverters);
    }

    /**
     * Returns the media types supported by all provided message converters preserving their ordering and
     * further sorting by specificity via {@link MediaType#sortBySpecificity(List)}.
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
     * Creates the method argument value of the expected parameter type by reading from the given request.
     *
     * @param <T> the expected type of the argument value to be created
     * @param webRequest the current request
     * @param methodParam the method argument
     * @param paramType the type of the argument value to be created
     * @return the created method argument value
     * @throws IOException if the reading from the request fails
     * @throws HttpMediaTypeNotSupportedException if no suitable message converter is found
     */
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter methodParam, Class<T> paramType) throws IOException,
            HttpMediaTypeNotSupportedException {

        HttpInputMessage inputMessage = createInputMessage(webRequest);
        return readWithMessageConverters(inputMessage, methodParam, paramType);
    }

    /**
     * Creates the method argument value of the expected parameter type by reading from the given HttpInputMessage.
     *
     * @param <T> the expected type of the argument value to be created
     * @param inputMessage the HTTP input message representing the current request
     * @param methodParam the method argument
     * @param paramType the type of the argument value to be created
     * @return the created method argument value
     * @throws IOException if the reading from the request fails
     * @throws HttpMediaTypeNotSupportedException if no suitable message converter is found
     */
    @SuppressWarnings("unchecked")
    protected <T> Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter methodParam, Class<T> paramType) throws IOException,
            HttpMediaTypeNotSupportedException {

        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
            if (messageConverter.canRead(paramType, contentType)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Reading [" + paramType.getName() + "] as \"" + contentType + "\" using [" +
                            messageConverter + "]");
                }
                return ((HttpMessageConverter<T>) messageConverter).read(paramType, inputMessage);
            }
        }

        throw new HttpMediaTypeNotSupportedException(contentType, allSupportedMediaTypes);
    }

    /**
     * Creates a new {@link HttpInputMessage} from the given {@link NativeWebRequest}.
     *
     * @param webRequest the web request to create an input message from
     * @return the input message
     */
    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

}
