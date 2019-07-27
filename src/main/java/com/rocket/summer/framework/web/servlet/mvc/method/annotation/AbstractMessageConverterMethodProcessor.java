package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.server.ServletServerHttpRequest;
import com.rocket.summer.framework.http.server.ServletServerHttpResponse;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Extends {@link AbstractMessageConverterMethodArgumentResolver} with the ability to handle method return
 * values by writing to the response with {@link HttpMessageConverter}s.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver
        implements HandlerMethodReturnValueHandler {

    private static final MediaType MEDIA_TYPE_APPLICATION = new MediaType("application");

    protected AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    /**
     * Creates a new {@link HttpOutputMessage} from the given {@link NativeWebRequest}.
     *
     * @param webRequest the web request to create an output message from
     * @return the output message
     */
    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        return new ServletServerHttpResponse(response);
    }

    /**
     * Writes the given return value to the given web request. Delegates to
     * {@link #writeWithMessageConverters(Object, MethodParameter, ServletServerHttpRequest, ServletServerHttpResponse)}
     */
    protected <T> void writeWithMessageConverters(T returnValue,
                                                  MethodParameter returnType,
                                                  NativeWebRequest webRequest)
            throws IOException, HttpMediaTypeNotAcceptableException {
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
    }

    /**
     * Writes the given return type to the given output message.
     *
     * @param returnValue the value to write to the output message
     * @param returnType the type of the value
     * @param inputMessage the input messages. Used to inspect the {@code Accept} header.
     * @param outputMessage the output message to write to
     * @throws IOException thrown in case of I/O errors
     * @throws HttpMediaTypeNotAcceptableException thrown when the conditions indicated by {@code Accept} header on
     * the request cannot be met by the message converters
     */
    @SuppressWarnings("unchecked")
    protected <T> void writeWithMessageConverters(T returnValue,
                                                  MethodParameter returnType,
                                                  ServletServerHttpRequest inputMessage,
                                                  ServletServerHttpResponse outputMessage)
            throws IOException, HttpMediaTypeNotAcceptableException {

        Class<?> returnValueClass = returnValue.getClass();

        List<MediaType> acceptableMediaTypes = getAcceptableMediaTypes(inputMessage);
        List<MediaType> producibleMediaTypes = getProducibleMediaTypes(inputMessage.getServletRequest(), returnValueClass);

        Set<MediaType> compatibleMediaTypes = new LinkedHashSet<MediaType>();
        for (MediaType a : acceptableMediaTypes) {
            for (MediaType p : producibleMediaTypes) {
                if (a.isCompatibleWith(p)) {
                    compatibleMediaTypes.add(getMostSpecificMediaType(a, p));
                }
            }
        }
        if (compatibleMediaTypes.isEmpty()) {
            throw new HttpMediaTypeNotAcceptableException(producibleMediaTypes);
        }

        List<MediaType> mediaTypes = new ArrayList<MediaType>(compatibleMediaTypes);
        MediaType.sortBySpecificityAndQuality(mediaTypes);

        MediaType selectedMediaType = null;
        for (MediaType mediaType : mediaTypes) {
            if (mediaType.isConcrete()) {
                selectedMediaType = mediaType;
                break;
            }
            else if (mediaType.equals(MediaType.ALL) || mediaType.equals(MEDIA_TYPE_APPLICATION)) {
                selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            }
        }

        if (selectedMediaType != null) {
            selectedMediaType = selectedMediaType.removeQualityValue();
            for (HttpMessageConverter<?> messageConverter : messageConverters) {
                if (messageConverter.canWrite(returnValueClass, selectedMediaType)) {
                    ((HttpMessageConverter<T>) messageConverter).write(returnValue, selectedMediaType, outputMessage);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Written [" + returnValue + "] as \"" + selectedMediaType + "\" using [" +
                                messageConverter + "]");
                    }
                    return;
                }
            }
        }
        throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
    }

    /**
     * Returns the media types that can be produced:
     * <ul>
     * 	<li>The producible media types specified in the request mappings, or
     * 	<li>Media types of configured converters that can write the specific return value, or
     * 	<li>{@link MediaType#ALL}
     * </ul>
     */
    @SuppressWarnings("unchecked")
    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> returnValueClass) {
        Set<MediaType> mediaTypes = (Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList<MediaType>(mediaTypes);
        }
        else if (!allSupportedMediaTypes.isEmpty()) {
            List<MediaType> result = new ArrayList<MediaType>();
            for (HttpMessageConverter<?> converter : messageConverters) {
                if (converter.canWrite(returnValueClass, null)) {
                    result.addAll(converter.getSupportedMediaTypes());
                }
            }
            return result;
        }
        else {
            return Collections.singletonList(MediaType.ALL);
        }
    }

    private List<MediaType> getAcceptableMediaTypes(HttpInputMessage inputMessage) {
        try {
            List<MediaType> result = inputMessage.getHeaders().getAccept();
            return result.isEmpty() ? Collections.singletonList(MediaType.ALL) : result;
        }
        catch (IllegalArgumentException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not parse Accept header: " + ex.getMessage());
            }
            return Collections.emptyList();
        }
    }

    /**
     * Return the more specific of the acceptable and the producible media types
     * with the q-value of the former.
     */
    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        produceType = produceType.copyQualityValue(acceptType);
        return MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceType) <= 0 ? acceptType : produceType;
    }

}