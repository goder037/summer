package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract implementation of the {@link ApplicationEventMulticaster} interface,
 * providing the basic listener registration facility.
 *
 * <p>Doesn't permit multiple instances of the same listener by default,
 * as it keeps listeners in a linked Set. The collection class used to hold
 * ApplicationListener objects can be overridden through the "collectionClass"
 * bean property.
 *
 * <p>Implementing ApplicationEventMulticaster's actual {@link #multicastEvent} method
 * is left to subclasses. {@link SimpleApplicationEventMulticaster} simply multicasts
 * all events to all registered listeners, invoking them in the calling thread.
 * Alternative implementations could be more sophisticated in those respects.
 *
 * @author Juergen Hoeller
 * @since 1.2.3
 * @see #setCollectionClass
 * @see #getApplicationListeners()
 * @see SimpleApplicationEventMulticaster
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {

    private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);

    /** Collection of ApplicationListeners */
    private Collection applicationListeners = new LinkedHashSet();

    final Map<ListenerCacheKey, ListenerRetriever> retrieverCache =
            new ConcurrentHashMap<ListenerCacheKey, ListenerRetriever>(64);

    private ClassLoader beanClassLoader;

    private BeanFactory beanFactory;

    private Object retrievalMutex = this.defaultRetriever;


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            if (this.beanClassLoader == null) {
                this.beanClassLoader = cbf.getBeanClassLoader();
            }
            this.retrievalMutex = cbf.getSingletonMutex();
        }
    }

    private BeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans " +
                    "because it is not associated with a BeanFactory");
        }
        return this.beanFactory;
    }


    /**
     * Set whether this multicaster should expect concurrent updates at runtime
     * (i.e. after context startup finished). In case of concurrent updates,
     * a copy-on-write strategy is applied, keeping iteration (for multicasting)
     * without synchronization while still making listener updates thread-safe.
     */
    public void setConcurrentUpdates(boolean concurrent) {
        Collection newColl = (concurrent ? CollectionFactory.createCopyOnWriteSet() : new LinkedHashSet());
        // Add all previously registered listeners (usually none).
        newColl.addAll(this.applicationListeners);
        this.applicationListeners = newColl;
    }

    /**
     * Specify the collection class to use. Can be populated with a fully
     * qualified class name when defined in a Spring application context.
     * <p>Default is a linked HashSet, keeping the registration order.
     * Note that a Set class specified will not permit multiple instances
     * of the same listener, while a List class will allow for registering
     * the same listener multiple times.
     */
    public void setCollectionClass(Class collectionClass) {
        if (collectionClass == null) {
            throw new IllegalArgumentException("'collectionClass' must not be null");
        }
        if (!Collection.class.isAssignableFrom(collectionClass)) {
            throw new IllegalArgumentException("'collectionClass' must implement [java.util.Collection]");
        }
        // Create desired collection instance.
        Collection newColl = (Collection) BeanUtils.instantiateClass(collectionClass);
        // Add all previously registered listeners (usually none).
        newColl.addAll(this.applicationListeners);
        this.applicationListeners = newColl;
    }


    public void addApplicationListener(ApplicationListener listener) {
        this.applicationListeners.add(listener);
    }

    public void removeApplicationListener(ApplicationListener listener) {
        this.applicationListeners.remove(listener);
    }

    public void removeAllListeners() {
        this.applicationListeners.clear();
    }

    /**
     * Return the current Collection of ApplicationListeners.
     * <p>Note that this is the raw Collection of ApplicationListeners,
     * potentially modified when new listeners get registered or
     * existing ones get removed. This Collection is not a snapshot copy.
     * @return a Collection of ApplicationListeners
     * @see com.rocket.summer.framework.context.ApplicationListener
     */
    protected Collection getApplicationListeners() {
        return this.applicationListeners;
    }

    /**
     * Return a Collection of ApplicationListeners matching the given
     * event type. Non-matching listeners get excluded early.
     * @param event the event to be propagated. Allows for excluding
     * non-matching listeners early, based on cached matching information.
     * @param eventType the event type
     * @return a Collection of ApplicationListeners
     * @see com.rocket.summer.framework.context.ApplicationListener
     */
    protected Collection<ApplicationListener<?>> getApplicationListeners(
            ApplicationEvent event, ResolvableType eventType) {

        Object source = event.getSource();
        Class<?> sourceType = (source != null ? source.getClass() : null);
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);

        // Quick check for existing entry on ConcurrentHashMap...
        ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getApplicationListeners();
        }

        if (this.beanClassLoader == null ||
                (ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) &&
                        (sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader)))) {
            // Fully synchronized building and caching of a ListenerRetriever
            synchronized (this.retrievalMutex) {
                retriever = this.retrieverCache.get(cacheKey);
                if (retriever != null) {
                    return retriever.getApplicationListeners();
                }
                retriever = new ListenerRetriever(true);
                Collection<ApplicationListener<?>> listeners =
                        retrieveApplicationListeners(eventType, sourceType, retriever);
                this.retrieverCache.put(cacheKey, retriever);
                return listeners;
            }
        }
        else {
            // No ListenerRetriever caching -> no synchronization necessary
            return retrieveApplicationListeners(eventType, sourceType, null);
        }
    }

    /**
     * Actually retrieve the application listeners for the given event and source type.
     * @param eventType the event type
     * @param sourceType the event source type
     * @param retriever the ListenerRetriever, if supposed to populate one (for caching purposes)
     * @return the pre-filtered list of application listeners for the given event and source type
     */
    private Collection<ApplicationListener<?>> retrieveApplicationListeners(
            ResolvableType eventType, Class<?> sourceType, ListenerRetriever retriever) {

        List<ApplicationListener<?>> allListeners = new ArrayList<ApplicationListener<?>>();
        Set<ApplicationListener<?>> listeners;
        Set<String> listenerBeans;
        synchronized (this.retrievalMutex) {
            listeners = new LinkedHashSet<ApplicationListener<?>>(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet<String>(this.defaultRetriever.applicationListenerBeans);
        }
        for (ApplicationListener<?> listener : listeners) {
            if (supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    retriever.applicationListeners.add(listener);
                }
                allListeners.add(listener);
            }
        }
        if (!listenerBeans.isEmpty()) {
            BeanFactory beanFactory = getBeanFactory();
            for (String listenerBeanName : listenerBeans) {
                try {
                    Class<?> listenerType = beanFactory.getType(listenerBeanName);
                    if (listenerType == null || supportsEvent(listenerType, eventType)) {
                        ApplicationListener<?> listener =
                                beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (!allListeners.contains(listener) && supportsEvent(listener, eventType, sourceType)) {
                            if (retriever != null) {
                                retriever.applicationListenerBeans.add(listenerBeanName);
                            }
                            allListeners.add(listener);
                        }
                    }
                }
                catch (NoSuchBeanDefinitionException ex) {
                    // Singleton listener instance (without backing bean definition) disappeared -
                    // probably in the middle of the destruction phase
                }
            }
        }
        AnnotationAwareOrderComparator.sort(allListeners);
        return allListeners;
    }

    /**
     * Filter a listener early through checking its generically declared event
     * type before trying to instantiate it.
     * <p>If this method returns {@code true} for a given listener as a first pass,
     * the listener instance will get retrieved and fully evaluated through a
     * {@link #supportsEvent(ApplicationListener, ResolvableType, Class)} call afterwards.
     * @param listenerType the listener's type as determined by the BeanFactory
     * @param eventType the event type to check
     * @return whether the given listener should be included in the candidates
     * for the given event type
     */
    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        if (GenericApplicationListener.class.isAssignableFrom(listenerType) ||
                SmartApplicationListener.class.isAssignableFrom(listenerType)) {
            return true;
        }
        ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
        return (declaredEventType == null || declaredEventType.isAssignableFrom(eventType));
    }

    /**
     * Determine whether the given listener supports the given event.
     * <p>The default implementation detects the {@link SmartApplicationListener}
     * and {@link GenericApplicationListener} interfaces. In case of a standard
     * {@link ApplicationListener}, a {@link GenericApplicationListenerAdapter}
     * will be used to introspect the generically declared type of the target listener.
     * @param listener the target listener to check
     * @param eventType the event type to check against
     * @param sourceType the source type to check against
     * @return whether the given listener should be included in the candidates
     * for the given event type
     */
    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, Class<?> sourceType) {
        GenericApplicationListener smartListener = (listener instanceof GenericApplicationListener ?
                (GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener));
        return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
    }

    /**
     * Cache key for ListenerRetrievers, based on event type and source type.
     */
    private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {

        private final ResolvableType eventType;

        private final Class<?> sourceType;

        public ListenerCacheKey(ResolvableType eventType, Class<?> sourceType) {
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            ListenerCacheKey otherKey = (ListenerCacheKey) other;
            return (ObjectUtils.nullSafeEquals(this.eventType, otherKey.eventType) &&
                    ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType));
        }

        @Override
        public int hashCode() {
            return (ObjectUtils.nullSafeHashCode(this.eventType) * 29 + ObjectUtils.nullSafeHashCode(this.sourceType));
        }

        @Override
        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType.getName() + "]";
        }

        @Override
        public int compareTo(ListenerCacheKey other) {
            int result = 0;
            if (this.eventType != null) {
                result = this.eventType.toString().compareTo(other.eventType.toString());
            }
            if (result == 0 && this.sourceType != null) {
                result = this.sourceType.getName().compareTo(other.sourceType.getName());
            }
            return result;
        }
    }

    /**
     * Helper class that encapsulates a specific set of target listeners,
     * allowing for efficient retrieval of pre-filtered listeners.
     * <p>An instance of this helper gets cached per event type and source type.
     */
    private class ListenerRetriever {

        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<ApplicationListener<?>>();

        public final Set<String> applicationListenerBeans = new LinkedHashSet<String>();

        private final boolean preFiltered;

        public ListenerRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            List<ApplicationListener<?>> allListeners = new ArrayList<ApplicationListener<?>>(
                    this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = getBeanFactory();
                for (String listenerBeanName : this.applicationListenerBeans) {
                    try {
                        ApplicationListener<?> listener = beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (this.preFiltered || !allListeners.contains(listener)) {
                            allListeners.add(listener);
                        }
                    }
                    catch (NoSuchBeanDefinitionException ex) {
                        // Singleton listener instance (without backing bean definition) disappeared -
                        // probably in the middle of the destruction phase
                    }
                }
            }
            AnnotationAwareOrderComparator.sort(allListeners);
            return allListeners;
        }
    }

}

