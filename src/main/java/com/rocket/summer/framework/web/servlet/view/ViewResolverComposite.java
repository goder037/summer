package com.rocket.summer.framework.web.servlet.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.web.context.ServletContextAware;
import com.rocket.summer.framework.web.servlet.View;
import com.rocket.summer.framework.web.servlet.ViewResolver;

/**
 * A {@link com.rocket.summer.framework.web.servlet.ViewResolver} that delegates to others.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class ViewResolverComposite implements ViewResolver, Ordered, InitializingBean,
        ApplicationContextAware, ServletContextAware {

    private final List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    private int order = Ordered.LOWEST_PRECEDENCE;


    /**
     * Set the list of view viewResolvers to delegate to.
     */
    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers.clear();
        if (!CollectionUtils.isEmpty(viewResolvers)) {
            this.viewResolvers.addAll(viewResolvers);
        }
    }

    /**
     * Return the list of view viewResolvers to delegate to.
     */
    public List<ViewResolver> getViewResolvers() {
        return Collections.unmodifiableList(this.viewResolvers);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (viewResolver instanceof ApplicationContextAware) {
                ((ApplicationContextAware)viewResolver).setApplicationContext(applicationContext);
            }
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (viewResolver instanceof ServletContextAware) {
                ((ServletContextAware)viewResolver).setServletContext(servletContext);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (viewResolver instanceof InitializingBean) {
                ((InitializingBean) viewResolver).afterPropertiesSet();
            }
        }
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, locale);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

}

