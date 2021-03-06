package com.rocket.summer.framework.web.servlet.mvc.support;

import com.rocket.summer.framework.beans.ConversionNotSupportedException;
import com.rocket.summer.framework.beans.TypeMismatchException;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.HttpMessageNotReadableException;
import com.rocket.summer.framework.http.converter.HttpMessageNotWritableException;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.HttpMediaTypeNotSupportedException;
import com.rocket.summer.framework.web.HttpRequestMethodNotSupportedException;
import com.rocket.summer.framework.web.bind.MethodArgumentNotValidException;
import com.rocket.summer.framework.web.bind.MissingServletRequestParameterException;
import com.rocket.summer.framework.web.bind.ServletRequestBindingException;
import com.rocket.summer.framework.web.bind.annotation.RequestBody;
import com.rocket.summer.framework.web.bind.annotation.RequestPart;
import com.rocket.summer.framework.web.multipart.MultipartFile;
import com.rocket.summer.framework.web.multipart.support.MissingServletRequestPartException;
import com.rocket.summer.framework.web.servlet.ModelAndView;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerExceptionResolver;
import com.rocket.summer.framework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Default implementation of the {@link com.rocket.summer.framework.web.servlet.HandlerExceptionResolver
 * HandlerExceptionResolver} interface that resolves standard Spring exceptions and translates
 * them to corresponding HTTP status codes.
 *
 * <p>This exception resolver is enabled by default in the {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.0
 * @see #handleNoSuchRequestHandlingMethod
 * @see #handleHttpRequestMethodNotSupported
 * @see #handleHttpMediaTypeNotSupported
 * @see #handleMissingServletRequestParameter
 * @see #handleServletRequestBindingException
 * @see #handleTypeMismatch
 * @see #handleHttpMessageNotReadable
 * @see #handleHttpMessageNotWritable
 * @see #handleMethodArgumentNotValidException
 */
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    /**
     * Log category to use when no mapped handler is found for a request.
     * @see #pageNotFoundLogger
     */
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "com.rocket.summer.framework.web.servlet.PageNotFound";

    /**
     * Additional logger to use when no mapped handler is found for a request.
     * @see #PAGE_NOT_FOUND_LOG_CATEGORY
     */
    protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);


    /**
     * Sets the {@linkplain #setOrder(int) order} to {@link #LOWEST_PRECEDENCE}.
     */
    public DefaultHandlerExceptionResolver() {
        setOrder(Ordered.LOWEST_PRECEDENCE);
    }


    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {

        try {
            if (ex instanceof NoSuchRequestHandlingMethodException) {
                return handleNoSuchRequestHandlingMethod((NoSuchRequestHandlingMethodException) ex, request, response,
                        handler);
            }
            else if (ex instanceof HttpRequestMethodNotSupportedException) {
                return handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex, request,
                        response, handler);
            }
            else if (ex instanceof HttpMediaTypeNotSupportedException) {
                return handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) ex, request, response,
                        handler);
            }
            else if (ex instanceof HttpMediaTypeNotAcceptableException) {
                return handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException) ex, request, response,
                        handler);
            }
            else if (ex instanceof MissingServletRequestParameterException) {
                return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, request,
                        response, handler);
            }
            else if (ex instanceof ServletRequestBindingException) {
                return handleServletRequestBindingException((ServletRequestBindingException) ex, request, response,
                        handler);
            }
            else if (ex instanceof ConversionNotSupportedException) {
                return handleConversionNotSupported((ConversionNotSupportedException) ex, request, response, handler);
            }
            else if (ex instanceof TypeMismatchException) {
                return handleTypeMismatch((TypeMismatchException) ex, request, response, handler);
            }
            else if (ex instanceof HttpMessageNotReadableException) {
                return handleHttpMessageNotReadable((HttpMessageNotReadableException) ex, request, response, handler);
            }
            else if (ex instanceof HttpMessageNotWritableException) {
                return handleHttpMessageNotWritable((HttpMessageNotWritableException) ex, request, response, handler);
            }
            else if (ex instanceof MethodArgumentNotValidException) {
                return handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex, request, response, handler);
            }
            else if (ex instanceof MissingServletRequestPartException) {
                return handleMissingServletRequestPartException((MissingServletRequestPartException) ex, request, response, handler);
            }
        }
        catch (Exception handlerException) {
            logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
        }
        return null;
    }

    /**
     * Handle the case where no request handler method was found.
     * <p>The default implementation logs a warning, sends an HTTP 404 error, and returns
     * an empty {@code ModelAndView}. Alternatively, a fallback view could be chosen,
     * or the NoSuchRequestHandlingMethodException could be rethrown as-is.
     * @param ex the NoSuchRequestHandlingMethodException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler, or <code>null</code> if none chosen
     * at the time of the exception (for example, if multipart resolution failed)
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleNoSuchRequestHandlingMethod(NoSuchRequestHandlingMethodException ex,
                                                             HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        pageNotFoundLogger.warn(ex.getMessage());
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return new ModelAndView();
    }

    /**
     * Handle the case where no request handler method was found for the particular HTTP request method.
     * <p>The default implementation logs a warning, sends an HTTP 405 error, sets the "Allow" header,
     * and returns an empty {@code ModelAndView}. Alternatively, a fallback view could be chosen,
     * or the HttpRequestMethodNotSupportedException could be rethrown as-is.
     * @param ex the HttpRequestMethodNotSupportedException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler, or <code>null</code> if none chosen
     * at the time of the exception (for example, if multipart resolution failed)
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                               HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        pageNotFoundLogger.warn(ex.getMessage());
        String[] supportedMethods = ex.getSupportedMethods();
        if (supportedMethods != null) {
            response.setHeader("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
        }
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex.getMessage());
        return new ModelAndView();
    }

    /**
     * Handle the case where no {@linkplain com.rocket.summer.framework.http.converter.HttpMessageConverter message converters}
     * were found for the PUT or POSTed content. <p>The default implementation sends an HTTP 415 error,
     * sets the "Accept" header, and returns an empty {@code ModelAndView}. Alternatively, a fallback
     * view could be chosen, or the HttpMediaTypeNotSupportedException could be rethrown as-is.
     * @param ex the HttpMediaTypeNotSupportedException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                           HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            response.setHeader("Accept", MediaType.toString(mediaTypes));
        }
        return new ModelAndView();
    }

    /**
     * Handle the case where no {@linkplain com.rocket.summer.framework.http.converter.HttpMessageConverter message converters}
     * were found that were acceptable for the client (expressed via the {@code Accept} header.
     * <p>The default implementation sends an HTTP 406 error and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotAcceptableException
     * could be rethrown as-is.
     * @param ex the HttpMediaTypeNotAcceptableException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                            HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
        return new ModelAndView();
    }

    /**
     * Handle the case when a required parameter is missing.
     * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the MissingServletRequestParameterException
     * could be rethrown as-is.
     * @param ex the MissingServletRequestParameterException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }

    /**
     * Handle the case when an unrecoverable binding exception occurs - e.g. required header, required cookie.
     * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the exception could be rethrown as-is.
     * @param ex the exception to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }

    /**
     * Handle the case when a {@link com.rocket.summer.framework.web.bind.WebDataBinder} conversion cannot occur.
     * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the TypeMismatchException could be rethrown as-is.
     * @param ex the ConversionNotSupportedException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleConversionNotSupported(ConversionNotSupportedException ex,
                                                        HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new ModelAndView();
    }

    /**
     * Handle the case when a {@link com.rocket.summer.framework.web.bind.WebDataBinder} conversion error occurs.
     * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the TypeMismatchException could be rethrown as-is.
     * @param ex the TypeMismatchException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleTypeMismatch(TypeMismatchException ex,
                                              HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }

    /**
     * Handle the case where a {@linkplain com.rocket.summer.framework.http.converter.HttpMessageConverter message converter}
     * cannot read from a HTTP request.
     * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotSupportedException could be
     * rethrown as-is.
     * @param ex the HttpMessageNotReadableException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                        HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }

    /**
     * Handle the case where a {@linkplain com.rocket.summer.framework.http.converter.HttpMessageConverter message converter}
     * cannot write to a HTTP request.
     * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotSupportedException could be
     * rethrown as-is.
     * @param ex the HttpMessageNotWritableException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
                                                        HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new ModelAndView();
    }

    /**
     * Handle the case where an argument annotated with {@code @Valid} such as
     * an {@link RequestBody} or {@link RequestPart} argument fails validation.
     * An HTTP 400 error is sent back to the client.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }

    /**
     * Handle the case where an @{@link RequestPart}, a {@link MultipartFile},
     * or a {@code javax.servlet.http.Part} argument is required but missing.
     * An HTTP 400 error is sent back to the client.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from response.sendError()
     */
    protected ModelAndView handleMissingServletRequestPartException(MissingServletRequestPartException ex,
                                                                    HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return new ModelAndView();
    }

}
