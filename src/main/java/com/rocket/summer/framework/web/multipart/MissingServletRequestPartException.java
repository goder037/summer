package com.rocket.summer.framework.web.multipart;

import javax.servlet.ServletException;

/**
 * Raised when the part of a "multipart/form-data" request identified by its
 * name cannot be found.
 *
 * <p>This may be because the request is not a multipart/form-data
 *
 * either because the part is not present in the request, or
 * because the web application is not configured correctly for processing
 * multipart requests -- e.g. no {@link MultipartResolver}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class MissingServletRequestPartException extends ServletException {

    private static final long serialVersionUID = -1255077391966870705L;

    private final String partName;

    public MissingServletRequestPartException(String partName) {
        super("Request part '" + partName + "' not found.");
        this.partName = partName;
    }

    public String getRequestPartName() {
        return this.partName;
    }
}

