package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.beans.factory.annotation.Value;

/**
 * Configuration properties for web error handling.
 *
 * @author Michael Stummvoll
 * @author Stephane Nicoll
 * @since 1.3.0
 */
public class ErrorProperties {

    /**
     * Path of the error controller.
     */
    @Value("${error.path:/error}")
    private String path = "/error";

    /**
     * When to include a "stacktrace" attribute.
     */
    private IncludeStacktrace includeStacktrace = IncludeStacktrace.NEVER;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public IncludeStacktrace getIncludeStacktrace() {
        return this.includeStacktrace;
    }

    public void setIncludeStacktrace(IncludeStacktrace includeStacktrace) {
        this.includeStacktrace = includeStacktrace;
    }

    /**
     * Include Stacktrace attribute options.
     */
    public enum IncludeStacktrace {

        /**
         * Never add stacktrace information.
         */
        NEVER,

        /**
         * Always add stacktrace information.
         */
        ALWAYS,

        /**
         * Add stacktrace information when the "trace" request parameter is "true".
         */
        ON_TRACE_PARAM

    }

}

