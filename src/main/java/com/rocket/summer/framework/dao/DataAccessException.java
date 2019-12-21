package com.rocket.summer.framework.dao;

import com.rocket.summer.framework.core.NestedRuntimeException;

public abstract class DataAccessException extends NestedRuntimeException {
    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

