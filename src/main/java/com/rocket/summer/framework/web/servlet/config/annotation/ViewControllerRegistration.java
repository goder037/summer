package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.http.HttpStatus;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.servlet.RequestToViewNameTranslator;
import com.rocket.summer.framework.web.servlet.mvc.ParameterizableViewController;

/**
 * Assist with the registration of a single view controller.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @since 3.1
 */
public class ViewControllerRegistration {

    private final String urlPath;

    private final ParameterizableViewController controller = new ParameterizableViewController();


    public ViewControllerRegistration(String urlPath) {
        Assert.notNull(urlPath, "'urlPath' is required.");
        this.urlPath = urlPath;
    }


    /**
     * Set the status code to set on the response. Optional.
     *
     * <p>If not set the response status will be 200 (OK).
     */
    public ViewControllerRegistration setStatusCode(HttpStatus statusCode) {
        this.controller.setStatusCode(statusCode);
        return this;
    }

    /**
     * Set the view name to return. Optional.
     *
     * <p>If not specified, the view controller will return {@code null} as the
     * view name in which case the configured {@link RequestToViewNameTranslator}
     * will select the view name. The {@code DefaultRequestToViewNameTranslator}
     * for example translates "/foo/bar" to "foo/bar".
     *
     * @see com.rocket.summer.framework.web.servlet.view.DefaultRequestToViewNameTranslator
     */
    public void setViewName(String viewName) {
        this.controller.setViewName(viewName);
    }

    protected void setApplicationContext(ApplicationContext applicationContext) {
        this.controller.setApplicationContext(applicationContext);
    }

    protected String getUrlPath() {
        return this.urlPath;
    }

    protected ParameterizableViewController getViewController() {
        return this.controller;
    }

}
