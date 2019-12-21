package com.rocket.summer.framework.web.multipart;

import com.rocket.summer.framework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides additional methods for dealing with multipart content within a
 * servlet request, allowing to access uploaded files.
 * Implementations also need to override the standard
 * {@link javax.servlet.ServletRequest} methods for parameter access, making
 * multipart parameters available.
 *
 * <p>A concrete implementation is
 * {@link com.rocket.summer.framework.web.multipart.support.DefaultMultipartHttpServletRequest}.
 * As an intermediate step,
 * {@link com.rocket.summer.framework.web.multipart.support.AbstractMultipartHttpServletRequest}
 * can be subclassed.
 *
 * @author Juergen Hoeller
 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see MultipartResolver
 * @see MultipartFile
 * @see javax.servlet.http.HttpServletRequest#getParameter
 * @see javax.servlet.http.HttpServletRequest#getParameterNames
 * @see javax.servlet.http.HttpServletRequest#getParameterMap
 * @see com.rocket.summer.framework.web.multipart.support.DefaultMultipartHttpServletRequest
 * @see com.rocket.summer.framework.web.multipart.support.AbstractMultipartHttpServletRequest
 */
public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {

    /**
     * Return the headers associated with the specified part of the multipart request.
     * <p>If the underlying implementation supports access to headers, then all headers are returned.
     * Otherwise, the returned headers will include a 'Content-Type' header at the very least.
     */
    HttpHeaders getMultipartHeaders(String paramOrFileName);

}
