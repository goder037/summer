package com.rocket.summer.framework.web.bind;

import com.rocket.summer.framework.web.util.NestedServletException;

/**
 * Fatal binding exception, thrown when we want to
 * treat binding exceptions as unrecoverable.
 *
 * <p>Extends ServletException for convenient throwing in any Servlet resource
 * (such as a Filter), and NestedServletException for proper root cause handling
 * (as the plain ServletException doesn't expose its root cause at all).
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class ServletRequestBindingException extends NestedServletException {

    /**
     * Constructor for ServletRequestBindingException.
     * @param msg the detail message
     */
    public ServletRequestBindingException(String msg) {
        super(msg);
    }

    /**
     * Constructor for ServletRequestBindingException.
     * @param msg the detail message
     * @param cause the root cause
     */
    public ServletRequestBindingException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

