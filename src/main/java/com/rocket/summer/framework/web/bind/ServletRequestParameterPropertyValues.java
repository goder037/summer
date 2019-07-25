package com.rocket.summer.framework.web.bind;

import com.rocket.summer.framework.beans.MutablePropertyValues;
import com.rocket.summer.framework.web.util.WebUtils;

import javax.servlet.ServletRequest;

/**
 * PropertyValues implementation created from parameters in a ServletRequest.
 * Can look for all property values beginning with a certain prefix and
 * prefix separator (default is "_").
 *
 * <p>For example, with a prefix of "spring", "spring_param1" and
 * "spring_param2" result in a Map with "param1" and "param2" as keys.
 *
 * <p>This class is not immutable to be able to efficiently remove property
 * values that should be ignored for binding.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.web.util.WebUtils#getParametersStartingWith
 */
public class ServletRequestParameterPropertyValues extends MutablePropertyValues {

    /** Default prefix separator */
    public static final String DEFAULT_PREFIX_SEPARATOR = "_";


    /**
     * Create new ServletRequestPropertyValues using no prefix
     * (and hence, no prefix separator).
     * @param request HTTP request
     */
    public ServletRequestParameterPropertyValues(ServletRequest request) {
        this(request, null, null);
    }

    /**
     * Create new ServletRequestPropertyValues using the given prefix and
     * the default prefix separator (the underscore character "_").
     * @param request HTTP request
     * @param prefix the prefix for parameters (the full prefix will
     * consist of this plus the separator)
     * @see #DEFAULT_PREFIX_SEPARATOR
     */
    public ServletRequestParameterPropertyValues(ServletRequest request, String prefix) {
        this(request, prefix, DEFAULT_PREFIX_SEPARATOR);
    }

    /**
     * Create new ServletRequestPropertyValues supplying both prefix and
     * prefix separator.
     * @param request HTTP request
     * @param prefix the prefix for parameters (the full prefix will
     * consist of this plus the separator)
     * @param prefixSeparator separator delimiting prefix (e.g. "spring")
     * and the rest of the parameter name ("param1", "param2")
     */
    public ServletRequestParameterPropertyValues(ServletRequest request, String prefix, String prefixSeparator) {
        super(WebUtils.getParametersStartingWith(
                request, (prefix != null ? prefix + prefixSeparator : null)));
    }

}

