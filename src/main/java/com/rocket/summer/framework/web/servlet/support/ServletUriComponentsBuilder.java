package com.rocket.summer.framework.web.servlet.support;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.context.request.RequestContextHolder;
import com.rocket.summer.framework.web.context.request.ServletRequestAttributes;
import com.rocket.summer.framework.web.util.UriComponentsBuilder;
import com.rocket.summer.framework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * A UriComponentsBuilder that extracts information from an HttpServletRequest.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletUriComponentsBuilder extends UriComponentsBuilder {

    /**
     * Default constructor. Protected to prevent direct instantiation.
     *
     * @see #fromContextPath(HttpServletRequest)
     * @see #fromServletMapping(HttpServletRequest)
     * @see #fromRequest(HttpServletRequest)
     * @see #fromCurrentContextPath()
     * @see #fromCurrentServletMapping()
     * @see #fromCurrentRequest()
     */
    protected ServletUriComponentsBuilder() {
    }

    /**
     * Prepare a builder from the host, port, scheme, and context path of
     * an HttpServletRequest.
     */
    public static ServletUriComponentsBuilder fromContextPath(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = fromRequest(request);
        builder.replacePath(request.getContextPath());
        builder.replaceQuery(null);
        return builder;
    }

    /**
     * Prepare a builder from the host, port, scheme, context path, and
     * servlet mapping of an HttpServletRequest. The results may vary depending
     * on the type of servlet mapping used.
     *
     * <p>If the servlet is mapped by name, e.g. {@code "/main/*"}, the path
     * will end with "/main". If the servlet is mapped otherwise, e.g.
     * {@code "/"} or {@code "*.do"}, the result will be the same as
     * if calling {@link #fromContextPath(HttpServletRequest)}.
     */
    public static ServletUriComponentsBuilder fromServletMapping(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = fromContextPath(request);
        if (StringUtils.hasText(new UrlPathHelper().getPathWithinServletMapping(request))) {
            builder.path(request.getServletPath());
        }
        return builder;
    }

    /**
     * Prepare a builder from the host, port, scheme, and path of
     * an HttpSevletRequest.
     */
    public static ServletUriComponentsBuilder fromRequestUri(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = fromRequest(request);
        builder.replacePath(request.getRequestURI());
        builder.replaceQuery(null);
        return builder;
    }

    /**
     * Prepare a builder by copying the scheme, host, port, path, and
     * query string of an HttpServletRequest.
     */
    public static ServletUriComponentsBuilder fromRequest(HttpServletRequest request) {
        String scheme = request.getScheme();
        int port = request.getServerPort();

        ServletUriComponentsBuilder builder = new ServletUriComponentsBuilder();
        builder.scheme(scheme);
        builder.host(request.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            builder.port(port);
        }
        builder.path(request.getRequestURI());
        builder.query(request.getQueryString());
        return builder;
    }

    /**
     * Same as {@link #fromContextPath(HttpServletRequest)} except the
     * request is obtained through {@link RequestContextHolder}.
     */
    public static ServletUriComponentsBuilder fromCurrentContextPath() {
        return fromContextPath(getCurrentRequest());
    }

    /**
     * Same as {@link #fromServletMapping(HttpServletRequest)} except the
     * request is obtained through {@link RequestContextHolder}.
     */
    public static ServletUriComponentsBuilder fromCurrentServletMapping() {
        return fromServletMapping(getCurrentRequest());
    }

    /**
     * Same as {@link #fromRequestUri(HttpServletRequest)} except the
     * request is obtained through {@link RequestContextHolder}.
     */
    public static ServletUriComponentsBuilder fromCurrentRequestUri() {
        return fromRequestUri(getCurrentRequest());
    }

    /**
     * Same as {@link #fromRequest(HttpServletRequest)} except the
     * request is obtained through {@link RequestContextHolder}.
     */
    public static ServletUriComponentsBuilder fromCurrentRequest() {
        return fromRequest(getCurrentRequest());
    }

    private static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
        return servletRequest;
    }

}

