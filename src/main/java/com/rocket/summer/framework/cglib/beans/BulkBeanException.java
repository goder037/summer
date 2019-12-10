package com.rocket.summer.framework.cglib.beans;

public class BulkBeanException extends RuntimeException
{
    private int index;
    private Throwable cause;

    public BulkBeanException(String message, int index) {
        super(message);
        this.index = index;
    }

    public BulkBeanException(Throwable cause, int index) {
        super(cause.getMessage());
        this.index = index;
        this.cause = cause;
    }

    public int getIndex() {
        return index;
    }

    public Throwable getCause() {
        return cause;
    }
}

