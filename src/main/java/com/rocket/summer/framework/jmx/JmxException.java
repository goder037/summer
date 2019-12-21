package com.rocket.summer.framework.jmx;

import com.rocket.summer.framework.context.NestedRuntimeException;

/**
 * General base exception to be thrown on JMX errors.
 * Unchecked since JMX failures are usually fatal.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class JmxException extends NestedRuntimeException {

    /**
     * Constructor for JmxException.
     * @param msg the detail message
     */
    public JmxException(String msg) {
        super(msg);
    }

    /**
     * Constructor for JmxException.
     * @param msg the detail message
     * @param cause the root cause (usually a raw JMX API exception)
     */
    public JmxException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
