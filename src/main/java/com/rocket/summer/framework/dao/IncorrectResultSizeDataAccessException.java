package com.rocket.summer.framework.dao;

/**
 * Data access exception thrown when a result was not of the expected size,
 * for example when expecting a single row but getting 0 or more than 1 rows.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.0.2
 * @see EmptyResultDataAccessException
 */
@SuppressWarnings("serial")
public class IncorrectResultSizeDataAccessException extends DataRetrievalFailureException {

    private int expectedSize;

    private int actualSize;


    /**
     * Constructor for IncorrectResultSizeDataAccessException.
     * @param expectedSize the expected result size
     */
    public IncorrectResultSizeDataAccessException(int expectedSize) {
        super("Incorrect result size: expected " + expectedSize);
        this.expectedSize = expectedSize;
        this.actualSize = -1;
    }

    /**
     * Constructor for IncorrectResultSizeDataAccessException.
     * @param expectedSize the expected result size
     * @param actualSize the actual result size (or -1 if unknown)
     */
    public IncorrectResultSizeDataAccessException(int expectedSize, int actualSize) {
        super("Incorrect result size: expected " + expectedSize + ", actual " + actualSize);
        this.expectedSize = expectedSize;
        this.actualSize = actualSize;
    }

    /**
     * Constructor for IncorrectResultSizeDataAccessException.
     * @param msg the detail message
     * @param expectedSize the expected result size
     */
    public IncorrectResultSizeDataAccessException(String msg, int expectedSize) {
        super(msg);
        this.expectedSize = expectedSize;
        this.actualSize = -1;
    }

    /**
     * Constructor for IncorrectResultSizeDataAccessException.
     * @param msg the detail message
     * @param expectedSize the expected result size
     * @param ex the wrapped exception
     */
    public IncorrectResultSizeDataAccessException(String msg, int expectedSize, Throwable ex) {
        super(msg, ex);
        this.expectedSize = expectedSize;
        this.actualSize = -1;
    }

    /**
     * Constructor for IncorrectResultSizeDataAccessException.
     * @param msg the detail message
     * @param expectedSize the expected result size
     * @param actualSize the actual result size (or -1 if unknown)
     */
    public IncorrectResultSizeDataAccessException(String msg, int expectedSize, int actualSize) {
        super(msg);
        this.expectedSize = expectedSize;
        this.actualSize = actualSize;
    }

    /**
     * Constructor for IncorrectResultSizeDataAccessException.
     * @param msg the detail message
     * @param expectedSize the expected result size
     * @param actualSize the actual result size (or -1 if unknown)
     * @param ex the wrapped exception
     */
    public IncorrectResultSizeDataAccessException(String msg, int expectedSize, int actualSize, Throwable ex) {
        super(msg, ex);
        this.expectedSize = expectedSize;
        this.actualSize = actualSize;
    }


    /**
     * Return the expected result size.
     */
    public int getExpectedSize() {
        return this.expectedSize;
    }

    /**
     * Return the actual result size (or -1 if unknown).
     */
    public int getActualSize() {
        return this.actualSize;
    }

}

