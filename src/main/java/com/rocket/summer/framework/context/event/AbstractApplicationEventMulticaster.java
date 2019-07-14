package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.CollectionFactory;

import java.util.Collection;
import java.util.LinkedHashSet;

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
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster {

    /** Collection of ApplicationListeners */
    private Collection applicationListeners = new LinkedHashSet();


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
     * @see org.springframework.context.ApplicationListener
     */
    protected Collection getApplicationListeners() {
        return this.applicationListeners;
    }

}

