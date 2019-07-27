package com.rocket.summer.framework.web.multipart;

import javax.servlet.http.HttpServletRequest;

/**
 * A strategy interface for multipart file upload resolution in accordance
 * with <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>.
 * Implementations are typically usable both within an application context
 * and standalone.
 *
 * <p>There is only one concrete implementation included in Spring,
 * as of Spring 2.5:
 * <ul>
 * <li>{@link com.rocket.summer.framework.web.multipart.commons.CommonsMultipartResolver} for Jakarta Commons FileUpload
 * </ul>
 *
 * <p>There is no default resolver implementation used for Spring
 * {@link com.rocket.summer.framework.web.servlet.DispatcherServlet DispatcherServlets},
 * as an application might choose to parse its multipart requests itself. To define
 * an implementation, create a bean with the id "multipartResolver" in a
 * {@link com.rocket.summer.framework.web.servlet.DispatcherServlet DispatcherServlet's}
 * application context. Such a resolver gets applied to all requests handled
 * by that {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}.
 *
 * <p>If a {@link com.rocket.summer.framework.web.servlet.DispatcherServlet} detects
 * a multipart request, it will resolve it via the configured
 * {@link com.rocket.summer.framework.web.multipart.MultipartResolver} and pass on a
 * wrapped {@link javax.servlet.http.HttpServletRequest}.
 * Controllers can then cast their given request to the
 * {@link com.rocket.summer.framework.web.multipart.MultipartHttpServletRequest}
 * interface, which permits access to any
 * {@link com.rocket.summer.framework.web.multipart.MultipartFile MultipartFiles}.
 * Note that this cast is only supported in case of an actual multipart request.
 *
 * <pre class="code">
 * public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
 *   MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
 *   MultipartFile multipartFile = multipartRequest.getFile("image");
 *   ...
 * }</pre>
 *
 * Instead of direct access, command or form controllers can register a
 * {@link com.rocket.summer.framework.web.multipart.support.ByteArrayMultipartFileEditor}
 * or {@link com.rocket.summer.framework.web.multipart.support.StringMultipartFileEditor}
 * with their data binder, to automatically apply multipart content to command
 * bean properties.
 *
 * <p>As an alternative to using a
 * {@link com.rocket.summer.framework.web.multipart.MultipartResolver} with a
 * {@link com.rocket.summer.framework.web.servlet.DispatcherServlet},
 * a {@link com.rocket.summer.framework.web.multipart.support.MultipartFilter} can be
 * registered in <code>web.xml</code>. It will delegate to a corresponding
 * {@link com.rocket.summer.framework.web.multipart.MultipartResolver} bean in the root
 * application context. This is mainly intended for applications that do not
 * use Spring's own web MVC framework.
 *
 * <p>Note: There is hardly ever a need to access the
 * {@link com.rocket.summer.framework.web.multipart.MultipartResolver} itself
 * from application code. It will simply do its work behind the scenes,
 * making
 * {@link com.rocket.summer.framework.web.multipart.MultipartHttpServletRequest MultipartHttpServletRequests}
 * available to controllers.
 *
 * @author Juergen Hoeller
 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see MultipartHttpServletRequest
 * @see MultipartFile
 * @see com.rocket.summer.framework.web.multipart.commons.CommonsMultipartResolver
 * @see com.rocket.summer.framework.web.multipart.support.ByteArrayMultipartFileEditor
 * @see com.rocket.summer.framework.web.multipart.support.StringMultipartFileEditor
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 */
public interface MultipartResolver {

    /**
     * Determine if the given request contains multipart content.
     * <p>Will typically check for content type "multipart/form-data", but the actually
     * accepted requests might depend on the capabilities of the resolver implementation.
     * @param request the servlet request to be evaluated
     * @return whether the request contains multipart content
     */
    boolean isMultipart(HttpServletRequest request);

    /**
     * Parse the given HTTP request into multipart files and parameters,
     * and wrap the request inside a
     * {@link com.rocket.summer.framework.web.multipart.MultipartHttpServletRequest} object
     * that provides access to file descriptors and makes contained
     * parameters accessible via the standard ServletRequest methods.
     * @param request the servlet request to wrap (must be of a multipart content type)
     * @return the wrapped servlet request
     * @throws MultipartException if the servlet request is not multipart, or if
     * implementation-specific problems are encountered (such as exceeding file size limits)
     * @see MultipartHttpServletRequest#getFile
     * @see MultipartHttpServletRequest#getFileNames
     * @see MultipartHttpServletRequest#getFileMap
     * @see javax.servlet.http.HttpServletRequest#getParameter
     * @see javax.servlet.http.HttpServletRequest#getParameterNames
     * @see javax.servlet.http.HttpServletRequest#getParameterMap
     */
    MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException;

    /**
     * Cleanup any resources used for the multipart handling,
     * like a storage for the uploaded files.
     * @param request the request to cleanup resources for
     */
    void cleanupMultipart(MultipartHttpServletRequest request);

}

