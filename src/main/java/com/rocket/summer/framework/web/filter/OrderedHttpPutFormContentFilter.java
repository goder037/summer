package com.rocket.summer.framework.web.filter;

import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.web.servlet.FilterRegistrationBean;

/**
 * {@link HttpPutFormContentFilter} that also implements {@link Ordered}.
 *
 * @author Joao Pedro Evangelista
 * @since 1.4.0
 */
public class OrderedHttpPutFormContentFilter extends HttpPutFormContentFilter
        implements Ordered {

    /**
     * Higher order to ensure the filter is applied before Spring Security.
     */
    public static final int DEFAULT_ORDER = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER
            - 9900;

    private int order = DEFAULT_ORDER;

    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Set the order for this filter.
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
