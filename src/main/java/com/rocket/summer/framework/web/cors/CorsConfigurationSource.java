package com.rocket.summer.framework.web.cors;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to be implemented by classes (usually HTTP request handlers) that
 * provides a {@link CorsConfiguration} instance based on the provided request.
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public interface CorsConfigurationSource {

    /**
     * Return a {@link CorsConfiguration} based on the incoming request.
     * @return the associated {@link CorsConfiguration}, or {@code null} if none
     */
    CorsConfiguration getCorsConfiguration(HttpServletRequest request);

}

