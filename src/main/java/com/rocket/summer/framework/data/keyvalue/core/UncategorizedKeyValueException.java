package com.rocket.summer.framework.data.keyvalue.core;

import com.rocket.summer.framework.dao.UncategorizedDataAccessException;

/**
 * @author Christoph Strobl
 */
public class UncategorizedKeyValueException extends UncategorizedDataAccessException {

    private static final long serialVersionUID = -8087116071859122297L;

    public UncategorizedKeyValueException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

