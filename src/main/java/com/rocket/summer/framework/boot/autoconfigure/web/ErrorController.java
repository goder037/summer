package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.stereotype.Controller;

/**
 * Marker interface used to indicate that a {@link Controller @Controller} is used to
 * render errors. Primarily used to know the error paths that will not need to be secured.
 *
 * @author Phillip Webb
 */
public interface ErrorController {

    /**
     * Returns the path of the error page.
     * @return the error path
     */
    String getErrorPath();

}
