package com.rocket.summer.framework.boot.context.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import com.rocket.summer.framework.context.ApplicationContextException;
import com.rocket.summer.framework.context.event.ApplicationEvent;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.event.SimpleApplicationEventMulticaster;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link ApplicationListener} that delegates to other listeners that are specified under
 * a {@literal context.listener.classes} environment property.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
public class DelegatingApplicationListener
        implements ApplicationListener<ApplicationEvent>, Ordered {

    // NOTE: Similar to com.rocket.summer.framework.web.context.ContextLoader

    private static final String PROPERTY_NAME = "context.listener.classes";

    private int order = 0;

    private SimpleApplicationEventMulticaster multicaster;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            List<ApplicationListener<ApplicationEvent>> delegates = getListeners(
                    ((ApplicationEnvironmentPreparedEvent) event).getEnvironment());
            if (delegates.isEmpty()) {
                return;
            }
            this.multicaster = new SimpleApplicationEventMulticaster();
            for (ApplicationListener<ApplicationEvent> listener : delegates) {
                this.multicaster.addApplicationListener(listener);
            }
        }
        if (this.multicaster != null) {
            this.multicaster.multicastEvent(event);
        }
    }

    @SuppressWarnings("unchecked")
    private List<ApplicationListener<ApplicationEvent>> getListeners(
            ConfigurableEnvironment environment) {
        if (environment == null) {
            return Collections.emptyList();
        }
        String classNames = environment.getProperty(PROPERTY_NAME);
        List<ApplicationListener<ApplicationEvent>> listeners = new ArrayList<ApplicationListener<ApplicationEvent>>();
        if (StringUtils.hasLength(classNames)) {
            for (String className : StringUtils.commaDelimitedListToSet(classNames)) {
                try {
                    Class<?> clazz = ClassUtils.forName(className,
                            ClassUtils.getDefaultClassLoader());
                    Assert.isAssignable(ApplicationListener.class, clazz, "class ["
                            + className + "] must implement ApplicationListener");
                    listeners.add((ApplicationListener<ApplicationEvent>) BeanUtils
                            .instantiateClass(clazz));
                }
                catch (Exception ex) {
                    throw new ApplicationContextException(
                            "Failed to load context listener class [" + className + "]",
                            ex);
                }
            }
        }
        AnnotationAwareOrderComparator.sort(listeners);
        return listeners;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

}

