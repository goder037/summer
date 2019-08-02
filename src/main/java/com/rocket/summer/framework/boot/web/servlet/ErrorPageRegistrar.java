package com.rocket.summer.framework.boot.web.servlet;

/**
 * Interface to be implemented by types that register {@link ErrorPage ErrorPages}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public interface ErrorPageRegistrar {

    /**
     * Register pages as required with the given registry.
     * @param registry the error page registry
     */
    void registerErrorPages(ErrorPageRegistry registry);

}

