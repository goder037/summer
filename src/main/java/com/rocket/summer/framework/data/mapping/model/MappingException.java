package com.rocket.summer.framework.data.mapping.model;

/**
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
public class MappingException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MappingException(String s) {
        super(s);
    }

    public MappingException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
