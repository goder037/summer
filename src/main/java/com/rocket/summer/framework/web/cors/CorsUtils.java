package com.rocket.summer.framework.web.cors;

import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpMethod;

/**
 * Utility class for CORS request handling based on the
 * <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>.
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public abstract class CorsUtils {

    /**
     * Returns {@code true} if the request is a valid CORS one.
     */
    public static boolean isCorsRequest(HttpServletRequest request) {
        return (request.getHeader(HttpHeaders.ORIGIN) != null);
    }

    /**
     * Returns {@code true} if the request is a valid CORS pre-flight one.
     */
    public static boolean isPreFlightRequest(HttpServletRequest request) {
        return (isCorsRequest(request) && HttpMethod.OPTIONS.matches(request.getMethod()) &&
                request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null);
    }

}

