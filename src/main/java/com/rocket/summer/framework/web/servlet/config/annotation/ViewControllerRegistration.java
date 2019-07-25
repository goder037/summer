package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.servlet.mvc.ParameterizableViewController;

/**
 * Encapsulates information required to create a view controller.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @since 3.1
 */
public class ViewControllerRegistration {

    private final String urlPath;

    private String viewName;

    /**
     * Creates a {@link ViewControllerRegistration} with the given URL path. When a request matches
     * to the given URL path this view controller will process it.
     */
    public ViewControllerRegistration(String urlPath) {
        Assert.notNull(urlPath, "A URL path is required to create a view controller.");
        this.urlPath = urlPath;
    }

    /**
     * Sets the view name to use for this view controller. This field is optional. If not specified the
     * view controller will return a {@code null} view name, which will be resolved through the configured
     * {@link RequestToViewNameTranslator}. By default that means "/foo/bar" would resolve to "foo/bar".
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * Returns the URL path for the view controller.
     */
    protected String getUrlPath() {
        return urlPath;
    }

    /**
     * Returns the view controllers.
     */
    protected Object getViewController() {
        ParameterizableViewController controller = new ParameterizableViewController();
        controller.setViewName(viewName);
        return controller;
    }

}

