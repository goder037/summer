package com.rocket.summer.framework.dao;

/**
 * Root of the hierarchy of data access exceptions that are considered non-transient -
 * where a retry of the same operation would fail unless the cause of the Exception
 * is corrected.
 *
 * @author Thomas Risberg
 * @since 2.5
 * @see java.sql.SQLNonTransientException
 */
@SuppressWarnings("serial")
public abstract class NonTransientDataAccessException extends DataAccessException {

    /**
     * Constructor for NonTransientDataAccessException.
     * @param msg the detail message
     */
    public NonTransientDataAccessException(String msg) {
        super(msg);
    }

    /**
     * Constructor for NonTransientDataAccessException.
     * @param msg the detail message
     * @param cause the root cause (usually from using a underlying
     * data access API such as JDBC)
     */
    public NonTransientDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

