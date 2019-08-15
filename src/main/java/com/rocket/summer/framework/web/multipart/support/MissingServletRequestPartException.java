package com.rocket.summer.framework.web.multipart.support;

import javax.servlet.ServletException;

import com.rocket.summer.framework.web.multipart.MultipartResolver;

/**
 * Raised when the part of a "multipart/form-data" request identified by its
 * name cannot be found.
 *
 * <p>This may be because the request is not a multipart/form-data request,
 * because the part is not present in the request, or because the web
 * application is not configured correctly for processing  multipart requests,
 * e.g. no {@link MultipartResolver}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
@SuppressWarnings("serial")
public class MissingServletRequestPartException extends ServletException {

    private final String partName;


    public MissingServletRequestPartException(String partName) {
        super("Required request part '" + partName + "' is not present");
        this.partName = partName;
    }


    public String getRequestPartName() {
        return this.partName;
    }

}

