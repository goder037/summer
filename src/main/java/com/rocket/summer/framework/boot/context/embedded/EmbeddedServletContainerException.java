package com.rocket.summer.framework.boot.context.embedded;

/**
 * Exceptions thrown by an embedded servlet container.
 *
 * @author Phillip Webb
 */
public class EmbeddedServletContainerException extends RuntimeException {

    public EmbeddedServletContainerException(String message, Throwable cause) {
        super(message, cause);
    }

}

