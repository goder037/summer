package com.rocket.summer.framework.boot.web.filter;

import com.rocket.summer.framework.boot.web.servlet.FilterRegistrationBean;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.web.filter.RequestContextFilter;

/**
 * {@link RequestContextFilter} that also implements {@link Ordered}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class OrderedRequestContextFilter extends RequestContextFilter implements Ordered {

    // Order defaults to after Spring Session filter
    private int order = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER - 105;

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
