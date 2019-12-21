package com.rocket.summer.framework.web.servlet;

/**
 * Provides additional information about a View such as whether it
 * performs redirects.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public interface SmartView extends View {

    /**
     * Whether the view performs a redirect.
     */
    boolean isRedirectView();

}