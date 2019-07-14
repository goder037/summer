package com.rocket.summer.framework.core;

/**
 * Interface that can be implemented by exceptions etc that are error coded.
 * The error code is a String, rather than a number, so it can be given
 * user-readable values, such as "object.failureDescription".
 *
 * <p>An error code can be resolved by a MessageSource, for example.
 *
 * @author Rod Johnson
 * @see org.springframework.context.MessageSource
 */
public interface ErrorCoded {

    /**
     * Return the error code associated with this failure.
     * The GUI can render this any way it pleases, allowing for localization etc.
     * @return a String error code associated with this failure,
     * or <code>null</code> if not error-coded
     */
    String getErrorCode();

}

