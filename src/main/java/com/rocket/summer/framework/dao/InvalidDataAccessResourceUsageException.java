package com.rocket.summer.framework.dao;

/**
 * Root for exceptions thrown when we use a data access resource incorrectly.
 * Thrown for example on specifying bad SQL when using a RDBMS.
 * Resource-specific subclasses are supplied by concrete data access packages.
 *
 * @author Rod Johnson
 */
@SuppressWarnings("serial")
public class InvalidDataAccessResourceUsageException extends NonTransientDataAccessException {

    /**
     * Constructor for InvalidDataAccessResourceUsageException.
     * @param msg the detail message
     */
    public InvalidDataAccessResourceUsageException(String msg) {
        super(msg);
    }

    /**
     * Constructor for InvalidDataAccessResourceUsageException.
     * @param msg the detail message
     * @param cause the root cause from the data access API in use
     */
    public InvalidDataAccessResourceUsageException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

