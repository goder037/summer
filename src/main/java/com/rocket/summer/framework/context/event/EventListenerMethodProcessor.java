package com.rocket.summer.framework.context.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.aop.framework.autoproxy.AutoProxyUtils;
import com.rocket.summer.framework.aop.scope.ScopedObject;
import com.rocket.summer.framework.aop.scope.ScopedProxyUtils;
import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.beans.factory.BeanInitializationException;
import com.rocket.summer.framework.beans.factory.SmartInitializingSingleton;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.MethodIntrospector;
import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * Registers {@link EventListener} methods as individual {@link ApplicationListener} instances.
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @since 4.2
 */
public class EventListenerMethodProcessor implements SmartInitializingSingleton, ApplicationContextAware {

    protected final Log logger = LogFactory.getLog(getClass());

    private ConfigurableApplicationContext applicationContext;

    private final EventExpressionEvaluator evaluator = new EventExpressionEvaluator();

    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext,
                "ApplicationContext does not implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        List<EventListenerFactory> factories = getEventListenerFactories();
        String[] beanNames = this.applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            if (!ScopedProxyUtils.isScopedTarget(beanName)) {
                Class<?> type = null;
                try {
                    type = AutoProxyUtils.determineTargetClass(this.applicationContext.getBeanFactory(), beanName);
                }
                catch (Throwable ex) {
                    // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                    }
                }
                if (type != null) {
                    if (ScopedObject.class.isAssignableFrom(type)) {
                        try {
                            type = AutoProxyUtils.determineTargetClass(this.applicationContext.getBeanFactory(),
                                    ScopedProxyUtils.getTargetBeanName(beanName));
                        }
                        catch (Throwable ex) {
                            // An invalid scoped proxy arrangement - let's ignore it.
                            if (logger.isDebugEnabled()) {
                                logger.debug("Could not resolve target bean for scoped proxy '" + beanName + "'", ex);
                            }
                        }
                    }
                    try {
                        processBean(factories, beanName, type);
                    }
                    catch (Throwable ex) {
                        throw new BeanInitializationException("Failed to process @EventListener " +
                                "annotation on bean with name '" + beanName + "'", ex);
                    }
                }
            }
        }
    }


    /**
     * Return the {@link EventListenerFactory} instances to use to handle
     * {@link EventListener} annotated methods.
     */
    protected List<EventListenerFactory> getEventListenerFactories() {
        Map<String, EventListenerFactory> beans = this.applicationContext.getBeansOfType(EventListenerFactory.class);
        List<EventListenerFactory> factories = new ArrayList<EventListenerFactory>(beans.values());
        AnnotationAwareOrderComparator.sort(factories);
        return factories;
    }

    protected void processBean(final List<EventListenerFactory> factories, final String beanName, final Class<?> targetType) {
        if (!this.nonAnnotatedClasses.contains(targetType)) {
            Map<Method, EventListener> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(targetType,
                        new MethodIntrospector.MetadataLookup<EventListener>() {
                            @Override
                            public EventListener inspect(Method method) {
                                return AnnotatedElementUtils.findMergedAnnotation(method, EventListener.class);
                            }
                        });
            }
            catch (Throwable ex) {
                // An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not resolve methods for bean with name '" + beanName + "'", ex);
                }
            }
            if (CollectionUtils.isEmpty(annotatedMethods)) {
                this.nonAnnotatedClasses.add(targetType);
                if (logger.isTraceEnabled()) {
                    logger.trace("No @EventListener annotations found on bean class: " + targetType.getName());
                }
            }
            else {
                // Non-empty set of methods
                for (Method method : annotatedMethods.keySet()) {
                    for (EventListenerFactory factory : factories) {
                        if (factory.supportsMethod(method)) {
                            Method methodToUse = AopUtils.selectInvocableMethod(
                                    method, this.applicationContext.getType(beanName));
                            ApplicationListener<?> applicationListener =
                                    factory.createApplicationListener(beanName, targetType, methodToUse);
                            if (applicationListener instanceof ApplicationListenerMethodAdapter) {
                                ((ApplicationListenerMethodAdapter) applicationListener)
                                        .init(this.applicationContext, this.evaluator);
                            }
                            this.applicationContext.addApplicationListener(applicationListener);
                            break;
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(annotatedMethods.size() + " @EventListener methods processed on bean '" +
                            beanName + "': " + annotatedMethods);
                }
            }
        }
    }

}

