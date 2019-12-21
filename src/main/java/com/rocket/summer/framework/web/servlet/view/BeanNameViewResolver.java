package com.rocket.summer.framework.web.servlet.view;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.web.context.support.WebApplicationObjectSupport;
import com.rocket.summer.framework.web.servlet.View;
import com.rocket.summer.framework.web.servlet.ViewResolver;

import java.util.Locale;

/**
 * A simple implementation of {@link com.rocket.summer.framework.web.servlet.ViewResolver}
 * that interprets a view name as a bean name in the current application context,
 * i.e. typically in the XML file of the executing {@code DispatcherServlet}.
 *
 * <p>This resolver can be handy for small applications, keeping all definitions
 * ranging from controllers to views in the same place. For larger applications,
 * {@link XmlViewResolver} will be the better choice, as it separates the XML
 * view bean definitions into a dedicated views file.
 *
 * <p>Note: Neither this {@code ViewResolver} nor {@link XmlViewResolver} supports
 * internationalization. Consider {@link ResourceBundleViewResolver} if you need
 * to apply different view resources per locale.
 *
 * <p>Note: This {@code ViewResolver} implements the {@link Ordered} interface
 * in order to allow for flexible participation in {@code ViewResolver} chaining.
 * For example, some special views could be defined via this {@code ViewResolver}
 * (giving it 0 as "order" value), while all remaining views could be resolved by
 * a {@link UrlBasedViewResolver}.
 *
 * @author Juergen Hoeller
 * @since 18.06.2003
 * @see XmlViewResolver
 * @see ResourceBundleViewResolver
 * @see UrlBasedViewResolver
 */
public class BeanNameViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {

    private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered


    /**
     * Specify the order value for this ViewResolver bean.
     * <p>The default value is {@code Ordered.LOWEST_PRECEDENCE}, meaning non-ordered.
     * @see com.rocket.summer.framework.core.Ordered#getOrder()
     */
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }


    @Override
    public View resolveViewName(String viewName, Locale locale) throws BeansException {
        ApplicationContext context = getApplicationContext();
        if (!context.containsBean(viewName)) {
            if (logger.isDebugEnabled()) {
                logger.debug("No matching bean found for view name '" + viewName + "'");
            }
            // Allow for ViewResolver chaining...
            return null;
        }
        if (!context.isTypeMatch(viewName, View.class)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found matching bean for view name '" + viewName +
                        "' - to be ignored since it does not implement View");
            }
            // Since we're looking into the general ApplicationContext here,
            // let's accept this as a non-match and allow for chaining as well...
            return null;
        }
        return context.getBean(viewName, View.class);
    }

}

