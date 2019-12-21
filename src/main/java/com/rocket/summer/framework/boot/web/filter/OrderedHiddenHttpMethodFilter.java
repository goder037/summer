package com.rocket.summer.framework.boot.web.filter;

import com.rocket.summer.framework.boot.web.servlet.FilterRegistrationBean;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.web.filter.HiddenHttpMethodFilter;

/**
 * {@link HiddenHttpMethodFilter} that also implements {@link Ordered}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class OrderedHiddenHttpMethodFilter extends HiddenHttpMethodFilter
        implements Ordered {

    /**
     * The default order is high to ensure the filter is applied before Spring Security.
     */
    public static final int DEFAULT_ORDER = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER
            - 10000;

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

