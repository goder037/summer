package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.beans.factory.BeanFactoryUtils;
import com.rocket.summer.framework.beans.factory.BeanInitializationException;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.web.accept.ContentNegotiationManager;
import com.rocket.summer.framework.web.servlet.View;
import com.rocket.summer.framework.web.servlet.ViewResolver;
import com.rocket.summer.framework.web.servlet.view.BeanNameViewResolver;
import com.rocket.summer.framework.web.servlet.view.ContentNegotiatingViewResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Assist with the configuration of a chain of
 * {@link com.rocket.summer.framework.web.servlet.ViewResolver ViewResolver} instances.
 * This class is expected to be used via {@link WebMvcConfigurer#configureViewResolvers}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class ViewResolverRegistry {

    private ContentNegotiationManager contentNegotiationManager;

    private ApplicationContext applicationContext;

    private ContentNegotiatingViewResolver contentNegotiatingResolver;

    private final List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>(4);

    private Integer order;


    /**
     * Class constructor with {@link ContentNegotiationManager} and {@link ApplicationContext}.
     * @since 4.3.12
     */
    public ViewResolverRegistry(ContentNegotiationManager contentNegotiationManager, ApplicationContext context) {
        this.contentNegotiationManager = contentNegotiationManager;
        this.applicationContext = context;
    }

    @Deprecated
    public ViewResolverRegistry() {
    }


    /**
     * Whether any view resolvers have been registered.
     */
    public boolean hasRegistrations() {
        return (this.contentNegotiatingResolver != null || !this.viewResolvers.isEmpty());
    }

    /**
     * Enable use of a {@link ContentNegotiatingViewResolver} to front all other
     * configured view resolvers and select among all selected Views based on
     * media types requested by the client (e.g. in the Accept header).
     * <p>If invoked multiple times the provided default views will be added to
     * any other default views that may have been configured already.
     * @see ContentNegotiatingViewResolver#setDefaultViews
     */
    public void enableContentNegotiation(View... defaultViews) {
        initContentNegotiatingViewResolver(defaultViews);
    }

    /**
     * Enable use of a {@link ContentNegotiatingViewResolver} to front all other
     * configured view resolvers and select among all selected Views based on
     * media types requested by the client (e.g. in the Accept header).
     * <p>If invoked multiple times the provided default views will be added to
     * any other default views that may have been configured already.
     * @see ContentNegotiatingViewResolver#setDefaultViews
     */
    public void enableContentNegotiation(boolean useNotAcceptableStatus, View... defaultViews) {
        initContentNegotiatingViewResolver(defaultViews);
        this.contentNegotiatingResolver.setUseNotAcceptableStatusCode(useNotAcceptableStatus);
    }

    private void initContentNegotiatingViewResolver(View[] defaultViews) {
        // ContentNegotiatingResolver in the registry: elevate its precedence!
        this.order = (this.order != null ? this.order : Ordered.HIGHEST_PRECEDENCE);

        if (this.contentNegotiatingResolver != null) {
            if (!ObjectUtils.isEmpty(defaultViews)) {
                if (!CollectionUtils.isEmpty(this.contentNegotiatingResolver.getDefaultViews())) {
                    List<View> views = new ArrayList<View>(this.contentNegotiatingResolver.getDefaultViews());
                    views.addAll(Arrays.asList(defaultViews));
                    this.contentNegotiatingResolver.setDefaultViews(views);
                }
            }
        }
        else {
            this.contentNegotiatingResolver = new ContentNegotiatingViewResolver();
            this.contentNegotiatingResolver.setDefaultViews(Arrays.asList(defaultViews));
            this.contentNegotiatingResolver.setViewResolvers(this.viewResolvers);
            this.contentNegotiatingResolver.setContentNegotiationManager(this.contentNegotiationManager);
        }
    }

    /**
     * Register a bean name view resolver that interprets view names as the names
     * of {@link com.rocket.summer.framework.web.servlet.View} beans.
     */
    public void beanName() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        this.viewResolvers.add(resolver);
    }

    /**
     * Register a {@link ViewResolver} bean instance. This may be useful to
     * configure a custom (or 3rd party) resolver implementation. It may also be
     * used as an alternative to other registration methods in this class when
     * they don't expose some more advanced property that needs to be set.
     */
    public void viewResolver(ViewResolver viewResolver) {
        if (viewResolver instanceof ContentNegotiatingViewResolver) {
            throw new BeanInitializationException(
                    "addViewResolver cannot be used to configure a ContentNegotiatingViewResolver. " +
                            "Please use the method enableContentNegotiation instead.");
        }
        this.viewResolvers.add(viewResolver);
    }

    /**
     * ViewResolver's registered through this registry are encapsulated in an
     * instance of {@link com.rocket.summer.framework.web.servlet.view.ViewResolverComposite
     * ViewResolverComposite} and follow the order of registration.
     * This property determines the order of the ViewResolverComposite itself
     * relative to any additional ViewResolver's (not registered here) present in
     * the Spring configuration
     * <p>By default this property is not set, which means the resolver is ordered
     * at {@link Ordered#LOWEST_PRECEDENCE} unless content negotiation is enabled
     * in which case the order (if not set explicitly) is changed to
     * {@link Ordered#HIGHEST_PRECEDENCE}.
     */
    public void order(int order) {
        this.order = order;
    }


    protected int getOrder() {
        return (this.order != null ? this.order : Ordered.LOWEST_PRECEDENCE);
    }

    protected List<ViewResolver> getViewResolvers() {
        if (this.contentNegotiatingResolver != null) {
            return Collections.<ViewResolver>singletonList(this.contentNegotiatingResolver);
        }
        else {
            return this.viewResolvers;
        }
    }

    private boolean checkBeanOfType(Class<?> beanType) {
        return (this.applicationContext == null ||
                !ObjectUtils.isEmpty(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                        this.applicationContext, beanType, false, false)));
    }

    @Deprecated
    protected boolean hasBeanOfType(Class<?> beanType) {
        return !ObjectUtils.isEmpty(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                this.applicationContext, beanType, false, false));
    }

    @Deprecated
    protected void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @Deprecated
    protected void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}

