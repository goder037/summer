package com.rocket.summer.framework.boot.web.servlet;

import com.rocket.summer.framework.util.Assert;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

/**
 * A {@link ServletContextInitializer} to register {@link Filter}s in a Servlet 3.0+
 * container. Similar to the {@link ServletContext#addFilter(String, Filter) registration}
 * features provided by {@link ServletContext} but with a Spring Bean friendly design.
 * <p>
 * The {@link #setFilter(Filter) Filter} must be specified before calling
 * {@link #onStartup(ServletContext)}. Registrations can be associated with
 * {@link #setUrlPatterns URL patterns} and/or servlets (either by {@link #setServletNames
 * name} or via a {@link #setServletRegistrationBeans ServletRegistrationBean}s. When no
 * URL pattern or servlets are specified the filter will be associated to '/*'. The filter
 * name will be deduced if not specified.
 *
 * @author Phillip Webb
 * @since 1.4.0
 * @see ServletContextInitializer
 * @see ServletContext#addFilter(String, Filter)
 * @see DelegatingFilterProxyRegistrationBean
 */
public class FilterRegistrationBean extends AbstractFilterRegistrationBean {

    /**
     * Filters that wrap the servlet request should be ordered less than or equal to this.
     */
    public static final int REQUEST_WRAPPER_FILTER_MAX_ORDER = AbstractFilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER;

    private Filter filter;

    /**
     * Create a new {@link FilterRegistrationBean} instance.
     */
    public FilterRegistrationBean() {
    }

    /**
     * Create a new {@link FilterRegistrationBean} instance to be registered with the
     * specified {@link ServletRegistrationBean}s.
     * @param filter the filter to register
     * @param servletRegistrationBeans associate {@link ServletRegistrationBean}s
     */
    public FilterRegistrationBean(Filter filter,
                                  ServletRegistrationBean... servletRegistrationBeans) {
        super(servletRegistrationBeans);
        Assert.notNull(filter, "Filter must not be null");
        this.filter = filter;
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * Set the filter to be registered.
     * @param filter the filter
     */
    public void setFilter(Filter filter) {
        Assert.notNull(filter, "Filter must not be null");
        this.filter = filter;
    }

}

