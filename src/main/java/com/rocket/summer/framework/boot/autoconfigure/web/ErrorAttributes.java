package com.rocket.summer.framework.boot.autoconfigure.web;

import java.util.Map;

import com.rocket.summer.framework.web.bind.annotation.ResponseBody;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.servlet.ModelAndView;

/**
 * Provides access to error attributes which can be logged or presented to the user.
 *
 * @author Phillip Webb
 * @since 1.1.0
 * @see DefaultErrorAttributes
 */
public interface ErrorAttributes {

    /**
     * Returns a {@link Map} of the error attributes. The map can be used as the model of
     * an error page {@link ModelAndView}, or returned as a {@link ResponseBody}.
     * @param requestAttributes the source request attributes
     * @param includeStackTrace if stack trace elements should be included
     * @return a map of error attributes
     */
    Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
                                           boolean includeStackTrace);

    /**
     * Return the underlying cause of the error or {@code null} if the error cannot be
     * extracted.
     * @param requestAttributes the source request attributes
     * @return the {@link Exception} that caused the error or {@code null}
     */
    Throwable getError(RequestAttributes requestAttributes);

}

