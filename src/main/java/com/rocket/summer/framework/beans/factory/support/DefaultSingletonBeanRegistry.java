package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.*;
import com.rocket.summer.framework.beans.factory.config.SingletonBeanRegistry;
import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.SimpleAliasRegistry;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic registry for shared bean instances, implementing the
 * {@link com.rocket.summer.framework.beans.factory.config.SingletonBeanRegistry}.
 * Allows for registering singleton instances that should be shared
 * for all callers of the registry, to be obtained via bean name.
 *
 * <p>Also supports registration of
 * {@link com.rocket.summer.framework.beans.factory.DisposableBean} instances,
 * (which might or might not correspond to registered singletons),
 * to be destroyed on shutdown of the registry. Dependencies between
 * beans can be registered to enforce an appropriate shutdown order.
 *
 * <p>This class mainly serves as base class for
 * {@link com.rocket.summer.framework.beans.factory.BeanFactory} implementations,
 * factoring out the common management of singleton bean instances. Note that
 * the {@link com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory}
 * interface extends the {@link SingletonBeanRegistry} interface.
 *
 * <p>Note that this class assumes neither a bean definition concept
 * nor a specific creation process for bean instances, in contrast to
 * {@link AbstractBeanFactory} and {@link DefaultListableBeanFactory}
 * (which inherit from it). Can alternatively also be used as a nested
 * helper to delegate to.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #registerSingleton
 * @see #registerDisposableBean
 * @see com.rocket.summer.framework.beans.factory.DisposableBean
 * @see com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory
 */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

    /**
     * Internal marker for a null singleton object:
     * used as marker value for concurrent Maps (which don't support null values).
     */
    protected static final Object NULL_OBJECT = new Object();


    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    /** Cache of singleton objects: bean name --> bean instance */
    private final Map singletonObjects = CollectionFactory.createConcurrentMapIfPossible(16);

    /** Cache of singleton factories: bean name --> ObjectFactory */
    private final Map singletonFactories = new HashMap();

    /** Cache of early singleton objects: bean name --> bean instance */
    private final Map earlySingletonObjects = new HashMap();

    /** Set of registered singletons, containing the bean names in registration order */
    private final Set registeredSingletons = new LinkedHashSet(16);

    /** Names of beans that are currently in creation */
    private final Set singletonsCurrentlyInCreation = Collections.synchronizedSet(new HashSet());

    /** List of suppressed Exceptions, available for associating related causes */
    private Set suppressedExceptions;

    /** Flag that indicates whether we're currently within destroySingletons */
    private boolean singletonsCurrentlyInDestruction = false;

    /** Disposable bean instances: bean name --> disposable instance */
    private final Map disposableBeans = new LinkedHashMap(16);

    /** Map between containing bean names: bean name --> Set of bean names that the bean contains */
    private final Map containedBeanMap = CollectionFactory.createConcurrentMapIfPossible(16);

    /** Names of beans currently excluded from in creation checks */
    private final Set<String> inCreationCheckExclusions =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));

    /** Map between dependent bean names: bean name --> Set of dependent bean names */
    /** Map between dependent bean names: bean name --> Set of dependent bean names */
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<String, Set<String>>(64);

    /** Map between depending bean names: bean name --> Set of bean names for the bean's dependencies */
    private final Map dependenciesForBeanMap = CollectionFactory.createConcurrentMapIfPossible(16);


    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    public void setCurrentlyInCreation(String beanName, boolean inCreation) {
        Assert.notNull(beanName, "Bean name must not be null");
        if (!inCreation) {
            this.inCreationCheckExclusions.add(beanName);
        }
        else {
            this.inCreationCheckExclusions.remove(beanName);
        }
    }

    /**
     * Add the given singleton object to the singleton cache of this factory.
     * <p>To be called for eager registration of singletons.
     * @param beanName the name of the bean
     * @param singletonObject the singleton object
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, (singletonObject != null ? singletonObject : NULL_OBJECT));
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    /**
     * Determine whether the specified dependent bean has been registered as
     * dependent on the given bean or on any of its transitive dependencies.
     * @param beanName the name of the bean to check
     * @param dependentBeanName the name of the dependent bean
     * @since 4.0
     */
    protected boolean isDependent(String beanName, String dependentBeanName) {
        synchronized (this.dependentBeanMap) {
            return isDependent(beanName, dependentBeanName, null);
        }
    }

    private boolean isDependent(String beanName, String dependentBeanName, Set<String> alreadySeen) {
        if (alreadySeen != null && alreadySeen.contains(beanName)) {
            return false;
        }
        String canonicalName = canonicalName(beanName);
        Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
        if (dependentBeans == null) {
            return false;
        }
        if (dependentBeans.contains(dependentBeanName)) {
            return true;
        }
        for (String transitiveDependency : dependentBeans) {
            if (alreadySeen == null) {
                alreadySeen = new HashSet<String>();
            }
            alreadySeen.add(beanName);
            if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add the given singleton factory for building the specified singleton
     * if necessary.
     * <p>To be called for eager registration of singletons, e.g. to be able to
     * resolve circular references.
     * @param beanName the name of the bean
     * @param singletonFactory the factory for the singleton object
     */
    protected void addSingletonFactory(String beanName, ObjectFactory singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }

    public boolean isCurrentlyInCreation(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return (!this.inCreationCheckExclusions.contains(beanName) && isActuallyInCreation(beanName));
    }

    protected boolean isActuallyInCreation(String beanName) {
        return isSingletonCurrentlyInCreation(beanName);
    }

    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    /**
     * Return the (raw) singleton object registered under the given name.
     * <p>Checks already instantiated singletons and also allows for an early
     * reference to a currently created singleton (resolving a circular reference).
     * @param beanName the name of the bean to look for
     * @param allowEarlyReference whether early references should be created or not
     * @return the registered singleton object, or <code>null</code> if none found
     */
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    ObjectFactory singletonFactory = (ObjectFactory) this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return (singletonObject != NULL_OBJECT ? singletonObject : null);
    }

    /**
     * Return the (raw) singleton object registered under the given name,
     * creating and registering a new one if none registered yet.
     * @param beanName the name of the bean
     * @param singletonFactory the ObjectFactory to lazily create the singleton
     * with, if necessary
     * @return the registered singleton object
     */
    public Object getSingleton(String beanName, ObjectFactory singletonFactory) {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(beanName,
                            "Singleton bean creation not allowed while the singletons of this factory are in destruction " +
                                    "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
                }
                beforeSingletonCreation(beanName);
                boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet();
                }
                try {
                    singletonObject = singletonFactory.getObject();
                }
                catch (BeanCreationException ex) {
                    if (recordSuppressedExceptions) {
                        for (Iterator it = this.suppressedExceptions.iterator(); it.hasNext();) {
                            ex.addRelatedCause((Exception) it.next());
                        }
                    }
                    throw ex;
                }
                finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    afterSingletonCreation(beanName);
                }
                addSingleton(beanName, singletonObject);
            }
            return (singletonObject != NULL_OBJECT ? singletonObject : null);
        }
    }

    /**
     * Register an Exception that happened to get suppressed during the creation of a
     * singleton bean instance, e.g. a temporary circular reference resolution problem.
     * @param ex the Exception to register
     */
    protected void onSuppressedException(Exception ex) {
        synchronized (this.singletonObjects) {
            if (this.suppressedExceptions != null) {
                this.suppressedExceptions.add(ex);
            }
        }
    }

    /**
     * Remove the bean with the given name from the singleton cache of this factory,
     * to be able to clean up eager registration of a singleton if creation failed.
     * @param beanName the name of the bean
     * @see #getSingletonMutex()
     */
    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    public boolean containsSingleton(String beanName) {
        return (this.singletonObjects.containsKey(beanName));
    }

    public String[] getSingletonNames() {
        synchronized (this.singletonObjects) {
            return StringUtils.toStringArray(this.registeredSingletons);
        }
    }

    public int getSingletonCount() {
        synchronized (this.singletonObjects) {
            return this.registeredSingletons.size();
        }
    }


    /**
     * Callback before singleton creation.
     * <p>Default implementation register the singleton as currently in creation.
     * @param beanName the name of the singleton about to be created
     * @see #isSingletonCurrentlyInCreation
     */
    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }

    /**
     * Callback after singleton creation.
     * <p>Default implementation marks the singleton as not in creation anymore.
     * @param beanName the name of the singleton that has been created
     * @see #isSingletonCurrentlyInCreation
     */
    protected void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }

    /**
     * Return whether the specified singleton bean is currently in creation
     * (within the entire factory).
     * @param beanName the name of the bean
     */
    public final boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }


    /**
     * Add the given bean to the list of disposable beans in this registry.
     * Disposable beans usually correspond to registered singletons,
     * matching the bean name but potentially being a different instance
     * (for example, a DisposableBean adapter for a singleton that does not
     * naturally implement Spring's DisposableBean interface).
     * @param beanName the name of the bean
     * @param bean the bean instance
     */
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        synchronized (this.disposableBeans) {
            this.disposableBeans.put(beanName, bean);
        }
    }

    /**
     * Register a containment relationship between two beans,
     * e.g. between an inner bean and its containing outer bean.
     * <p>Also registers the containing bean as dependent on the contained bean
     * in terms of destruction order.
     * @param containedBeanName the name of the contained (inner) bean
     * @param containingBeanName the name of the containing (outer) bean
     * @see #registerDependentBean
     */
    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        synchronized (this.containedBeanMap) {
            Set containedBeans = (Set) this.containedBeanMap.get(containingBeanName);
            if (containedBeans == null) {
                containedBeans = new LinkedHashSet(8);
                this.containedBeanMap.put(containingBeanName, containedBeans);
            }
            containedBeans.add(containedBeanName);
        }
        registerDependentBean(containedBeanName, containingBeanName);
    }

    /**
     * Register a dependent bean for the given bean,
     * to be destroyed before the given bean is destroyed.
     * @param beanName the name of the bean
     * @param dependentBeanName the name of the dependent bean
     */
    public void registerDependentBean(String beanName, String dependentBeanName) {
        synchronized (this.dependentBeanMap) {
            Set dependentBeans = (Set) this.dependentBeanMap.get(beanName);
            if (dependentBeans == null) {
                dependentBeans = new LinkedHashSet(8);
                this.dependentBeanMap.put(beanName, dependentBeans);
            }
            dependentBeans.add(dependentBeanName);
        }
        synchronized (this.dependenciesForBeanMap) {
            Set dependenciesForBean = (Set) this.dependenciesForBeanMap.get(dependentBeanName);
            if (dependenciesForBean == null) {
                dependenciesForBean = new LinkedHashSet(8);
                this.dependenciesForBeanMap.put(dependentBeanName, dependenciesForBean);
            }
            dependenciesForBean.add(beanName);
        }
    }

    /**
     * Determine whether a dependent bean has been registered for the given name.
     * @param beanName the name of the bean to check
     */
    protected boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }

    /**
     * Return the names of all beans which depend on the specified bean, if any.
     * @param beanName the name of the bean
     * @return the array of dependent bean names, or an empty array if none
     */
    public String[] getDependentBeans(String beanName) {
        Set dependentBeans = (Set) this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        }
        return (String[]) dependentBeans.toArray(new String[dependentBeans.size()]);
    }

    /**
     * Return the names of all beans that the specified bean depends on, if any.
     * @param beanName the name of the bean
     * @return the array of names of beans which the bean depends on,
     * or an empty array if none
     */
    public String[] getDependenciesForBean(String beanName) {
        Set dependenciesForBean = (Set) this.dependenciesForBeanMap.get(beanName);
        if (dependenciesForBean == null) {
            return new String[0];
        }
        return (String[]) dependenciesForBean.toArray(new String[dependenciesForBean.size()]);
    }

    public void destroySingletons() {
        if (logger.isInfoEnabled()) {
            logger.info("Destroying singletons in " + this);
        }
        synchronized (this.singletonObjects) {
            this.singletonsCurrentlyInDestruction = true;
        }

        synchronized (this.disposableBeans) {
            String[] disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
            for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
                destroySingleton(disposableBeanNames[i]);
            }
        }

        this.containedBeanMap.clear();
        this.dependentBeanMap.clear();
        this.dependenciesForBeanMap.clear();

        synchronized (this.singletonObjects) {
            this.singletonObjects.clear();
            this.singletonFactories.clear();
            this.earlySingletonObjects.clear();
            this.registeredSingletons.clear();
            this.singletonsCurrentlyInDestruction = false;
        }
    }

    /**
     * Destroy the given bean. Delegates to <code>destroyBean</code>
     * if a corresponding disposable bean instance is found.
     * @param beanName the name of the bean
     * @see #destroyBean
     */
    public void destroySingleton(String beanName) {
        // Remove a registered singleton of the given name, if any.
        removeSingleton(beanName);

        // Destroy the corresponding DisposableBean instance.
        DisposableBean disposableBean = null;
        synchronized (this.disposableBeans) {
            disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
        }
        destroyBean(beanName, disposableBean);
    }

    /**
     * Destroy the given bean. Must destroy beans that depend on the given
     * bean before the bean itself. Should not throw any exceptions.
     * @param beanName the name of the bean
     * @param bean the bean instance to destroy
     */
    protected void destroyBean(String beanName, DisposableBean bean) {
        // Trigger destruction of dependent beans first...
        Set dependencies = (Set) this.dependentBeanMap.remove(beanName);
        if (dependencies != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
            }
            for (Iterator it = dependencies.iterator(); it.hasNext();) {
                String dependentBeanName = (String) it.next();
                destroySingleton(dependentBeanName);
            }
        }

        // Actually destroy the bean now...
        if (bean != null) {
            try {
                bean.destroy();
            }
            catch (Throwable ex) {
                logger.error("Destroy method on bean with name '" + beanName + "' threw an exception", ex);
            }
        }

        // Trigger destruction of contained beans...
        Set containedBeans = (Set) this.containedBeanMap.remove(beanName);
        if (containedBeans != null) {
            for (Iterator it = containedBeans.iterator(); it.hasNext();) {
                String containedBeanName = (String) it.next();
                destroySingleton(containedBeanName);
            }
        }

        // Remove destroyed bean from other beans' dependencies.
        synchronized (this.dependentBeanMap) {
            for (Iterator it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                Set dependenciesToClean = (Set) entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }

        // Remove destroyed bean's prepared dependency information.
        this.dependenciesForBeanMap.remove(beanName);
    }

    /**
     * Expose the singleton mutex to subclasses.
     * <p>Subclasses should synchronize on the given Object if they perform
     * any sort of extended singleton creation phase. In particular, subclasses
     * should <i>not</i> have their own mutexes involved in singleton creation,
     * to avoid the potential for deadlocks in lazy-init situations.
     */
    protected final Object getSingletonMutex() {
        return this.singletonObjects;
    }

}

