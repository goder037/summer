package com.rocket.summer.framework.boot.web.servlet;

/**
 * Interface for a registry that holds {@link ErrorPage ErrorPages}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public interface ErrorPageRegistry {

    /**
     * Adds error pages that will be used when handling exceptions.
     * @param errorPages the error pages
     */
    void addErrorPages(ErrorPage... errorPages);

}
