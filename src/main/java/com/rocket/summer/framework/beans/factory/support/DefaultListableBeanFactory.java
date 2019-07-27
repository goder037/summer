package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.FatalBeanException;
import com.rocket.summer.framework.beans.TypeConverter;
import com.rocket.summer.framework.beans.factory.*;
import com.rocket.summer.framework.beans.factory.config.*;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.util.*;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    /** Whether to allow re-registration of a different definition with the same name */
    private boolean allowBeanDefinitionOverriding = true;

    /** Whether to allow eager class loading even for lazy-init beans */
    private boolean allowEagerClassLoading = true;

    /** Whether bean definition metadata may be cached for all beans */
    private boolean configurationFrozen = false;

    /** Map of bean definition objects, keyed by bean name */
    private final Map beanDefinitionMap = CollectionFactory.createConcurrentMapIfPossible(16);

    /** List of bean definition names, in registration order */
    private final List beanDefinitionNames = new ArrayList();

    /** Cached array of bean definition names in case of frozen configuration */
    private String[] frozenBeanDefinitionNames;

    /** Resolver to use for checking if a bean definition is an autowire candidate */
    private AutowireCandidateResolver autowireCandidateResolver = AutowireUtils.createAutowireCandidateResolver();

    /** Map from dependency type to corresponding autowired value */
    private final Map resolvableDependencies = new HashMap();


    /**
     * Create a new DefaultListableBeanFactory.
     */
    public DefaultListableBeanFactory() {
        super();
    }

    /**
     * Set a custom autowire candidate resolver for this BeanFactory to use
     * when deciding whether a bean definition should be considered as a
     * candidate for autowiring.
     */
    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
        this.autowireCandidateResolver = autowireCandidateResolver;
    }

    /**
     * Return the autowire candidate resolver for this BeanFactory (never <code>null</code>).
     */
    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return this.autowireCandidateResolver;
    }

    /**
     * Create a new DefaultListableBeanFactory with the given parent.
     * @param parentBeanFactory the parent BeanFactory
     */
    public DefaultListableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        synchronized (this.beanDefinitionMap) {
            if (this.frozenBeanDefinitionNames != null) {
                return this.frozenBeanDefinitionNames;
            }
            else {
                return StringUtils.toStringArray(this.beanDefinitionNames);
            }
        }
    }

    public String[] getBeanNamesForType(Class type) {
        return getBeanNamesForType(type, true, true);
    }

    public String[] getBeanNamesForType(Class type, boolean includeNonSingletons, boolean allowEagerInit) {
        List result = new ArrayList();

        // Check all bean definitions.
        String[] beanDefinitionNames = getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            String beanName = beanDefinitionName;
            // Only consider bean as eligible if the bean name
            // is not defined as alias for some other bean.
            if (!isAlias(beanName)) {
                try {
                    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                    // Only check bean definition if it is complete.
                    if (!mbd.isAbstract() &&
                            (allowEagerInit || ((mbd.hasBeanClass() || !mbd.isLazyInit() || this.allowEagerClassLoading)) &&
                                    !requiresEagerInitForType(mbd.getFactoryBeanName()))) {
                        // In case of FactoryBean, match object created by FactoryBean.
                        boolean isFactoryBean = isFactoryBean(beanName, mbd);
                        boolean matchFound =
                                (allowEagerInit || !isFactoryBean || containsSingleton(beanName)) &&
                                        (includeNonSingletons || isSingleton(beanName)) && isTypeMatch(beanName, type);
                        if (!matchFound && isFactoryBean) {
                            // In case of FactoryBean, try to match FactoryBean instance itself next.
                            beanName = FACTORY_BEAN_PREFIX + beanName;
                            matchFound = (includeNonSingletons || mbd.isSingleton()) && isTypeMatch(beanName, type);
                        }
                        if (matchFound) {
                            result.add(beanName);
                        }
                    }
                } catch (CannotLoadBeanClassException ex) {
                    if (allowEagerInit) {
                        throw ex;
                    }
                    // Probably contains a placeholder: let's ignore it for type matching purposes.
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring bean class loading failure for bean '" + beanName + "'", ex);
                    }
                    onSuppressedException(ex);
                } catch (BeanDefinitionStoreException ex) {
                    if (allowEagerInit) {
                        throw ex;
                    }
                    // Probably contains a placeholder: let's ignore it for type matching purposes.
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring unresolvable metadata in bean definition '" + beanName + "'", ex);
                    }
                    onSuppressedException(ex);
                }
            }
        }

        // Check singletons too, to catch manually registered singletons.
        String[] singletonNames = getSingletonNames();
        for (int i = 0; i < singletonNames.length; i++) {
            String beanName = singletonNames[i];
            // Only check if manually registered.
            if (!containsBeanDefinition(beanName)) {
                // In case of FactoryBean, match object created by FactoryBean.
                if (isFactoryBean(beanName)) {
                    if ((includeNonSingletons || isSingleton(beanName)) && isTypeMatch(beanName, type)) {
                        result.add(beanName);
                        // Match found for this bean: do not match FactoryBean itself anymore.
                        continue;
                    }
                    // In case of FactoryBean, try to match FactoryBean itself next.
                    beanName = FACTORY_BEAN_PREFIX + beanName;
                }
                // Match raw bean instance (might be raw FactoryBean).
                if (isTypeMatch(beanName, type)) {
                    result.add(beanName);
                }
            }
        }

        return StringUtils.toStringArray(result);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        String[] beanNames = getBeanNamesForType(requiredType);
        if (beanNames.length > 1) {
            ArrayList<String> autowireCandidates = new ArrayList<String>();
            for (String beanName : beanNames) {
                if (getBeanDefinition(beanName).isAutowireCandidate()) {
                    autowireCandidates.add(beanName);
                }
            }
            if (autowireCandidates.size() > 0) {
                beanNames = autowireCandidates.toArray(new String[autowireCandidates.size()]);
            }
        }
        if (beanNames.length == 1) {
            return getBean(beanNames[0], requiredType);
        }
        else if (beanNames.length == 0 && getParentBeanFactory() != null) {
            return getParentBeanFactory().getBean(requiredType);
        }
        else {
            throw new NoSuchBeanDefinitionException(requiredType, "expected single bean but found " +
                    beanNames.length + ": " + StringUtils.arrayToCommaDelimitedString(beanNames));
        }
    }

    /**
     * Check whether the specified bean would need to be eagerly initialized
     * in order to determine its type.
     * @param factoryBeanName a factory-bean reference that the bean definition
     * defines a factory method for
     * @return whether eager initialization is necessary
     */
    private boolean requiresEagerInitForType(String factoryBeanName) {
        return (factoryBeanName != null && isFactoryBean(factoryBeanName) && !containsSingleton(factoryBeanName));
    }

    public Map getBeansOfType(Class type) throws BeansException {
        return getBeansOfType(type, true, true);
    }

    public Map getBeansOfType(Class type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {

        String[] beanNames = getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        Map result = new LinkedHashMap(beanNames.length);
        for (int i = 0; i < beanNames.length; i++) {
            String beanName = beanNames[i];
            try {
                result.put(beanName, getBean(beanName));
            }
            catch (BeanCreationException ex) {
                Throwable rootCause = ex.getMostSpecificCause();
                if (rootCause instanceof BeanCurrentlyInCreationException) {
                    BeanCreationException bce = (BeanCreationException) rootCause;
                    if (isCurrentlyInCreation(bce.getBeanName())) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Ignoring match to currently created bean '" + beanName + "': " + ex.getMessage());
                        }
                        onSuppressedException(ex);
                        // Ignore: indicates a circular reference when autowiring constructors.
                        // We want to find matches other than the currently created bean itself.
                        continue;
                    }
                }
                throw ex;
            }
        }
        return result;
    }


    //---------------------------------------------------------------------
    // Implementation of ConfigurableListableBeanFactory interface
    //---------------------------------------------------------------------

    public void registerResolvableDependency(Class dependencyType, Object autowiredValue) {
        Assert.notNull(dependencyType, "Type must not be null");
        if (autowiredValue != null) {
            Assert.isTrue((autowiredValue instanceof ObjectFactory || dependencyType.isInstance(autowiredValue)),
                    "Value [" + autowiredValue + "] does not implement specified type [" + dependencyType.getName() + "]");
            this.resolvableDependencies.put(dependencyType, autowiredValue);
        }
    }

    public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
            throws NoSuchBeanDefinitionException {

        // Consider FactoryBeans as autowiring candidates.
        boolean isFactoryBean = (descriptor != null && descriptor.getDependencyType() != null &&
                FactoryBean.class.isAssignableFrom(descriptor.getDependencyType()));
        if (isFactoryBean) {
            beanName = BeanFactoryUtils.transformedBeanName(beanName);
        }

        if (!containsBeanDefinition(beanName)) {
            if (containsSingleton(beanName)) {
                return true;
            }
            else if (getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
                // No bean definition found in this factory -> delegate to parent.
                return ((ConfigurableListableBeanFactory) getParentBeanFactory()).isAutowireCandidate(beanName, descriptor);
            }
        }

        return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(beanName), descriptor);
    }

    /**
     * Determine whether the specified bean definition qualifies as an autowire candidate,
     * to be injected into other beans which declare a dependency of matching type.
     * @param beanName the name of the bean definition to check
     * @param mbd the merged bean definition to check
     * @param descriptor the descriptor of the dependency to resolve
     * @return whether the bean should be considered as autowire candidate
     */
    protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor) {
        resolveBeanClass(mbd, beanName);
        return getAutowireCandidateResolver().isAutowireCandidate(
                new BeanDefinitionHolder(mbd, beanName, getAliases(beanName)), descriptor);
    }

    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = (BeanDefinition) this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return bd;
    }

    public void freezeConfiguration() {
        this.configurationFrozen = true;
        synchronized (this.beanDefinitionMap) {
            this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
        }
    }

    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }

    /**
     * Considers all beans as eligible for metdata caching
     * if the factory's configuration has been marked as frozen.
     * @see #freezeConfiguration()
     */
    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return (this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName));
    }

    public void preInstantiateSingletons() throws BeansException {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Pre-instantiating singletons in " + this);
        }

        synchronized (this.beanDefinitionMap) {
            for (Iterator it = this.beanDefinitionNames.iterator(); it.hasNext();) {
                String beanName = (String) it.next();
                RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
                if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                    if (isFactoryBean(beanName)) {
                        FactoryBean factory = (FactoryBean) getBean(FACTORY_BEAN_PREFIX + beanName);
                        if (factory instanceof SmartFactoryBean && ((SmartFactoryBean) factory).isEagerInit()) {
                            getBean(beanName);
                        }
                    }
                    else {
                        getBean(beanName);
                    }
                }
            }
        }
    }


    //---------------------------------------------------------------------
    // Implementation of BeanDefinitionRegistry interface
    //---------------------------------------------------------------------

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {

        Assert.hasText(beanName, "'beanName' must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");

        if (beanDefinition instanceof AbstractBeanDefinition) {
            try {
                ((AbstractBeanDefinition) beanDefinition).validate();
            }
            catch (BeanDefinitionValidationException ex) {
                throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
                        "Validation of bean definition failed", ex);
            }
        }

        synchronized (this.beanDefinitionMap) {
            Object oldBeanDefinition = this.beanDefinitionMap.get(beanName);
            if (oldBeanDefinition != null) {
                if (!this.allowBeanDefinitionOverriding) {
                    throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
                            "Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName +
                                    "': There is already [" + oldBeanDefinition + "] bound.");
                }
                else {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("Overriding bean definition for bean '" + beanName +
                                "': replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
                    }
                }
            }
            else {
                this.beanDefinitionNames.add(beanName);
                this.frozenBeanDefinitionNames = null;
            }
            this.beanDefinitionMap.put(beanName, beanDefinition);

            resetBeanDefinition(beanName);
        }
    }

    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        Assert.hasText(beanName, "'beanName' must not be empty");

        synchronized (this.beanDefinitionMap) {
            BeanDefinition bd = (BeanDefinition) this.beanDefinitionMap.remove(beanName);
            if (bd == null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No bean named '" + beanName + "' found in " + this);
                }
                throw new NoSuchBeanDefinitionException(beanName);
            }
            this.beanDefinitionNames.remove(beanName);
            this.frozenBeanDefinitionNames = null;

            resetBeanDefinition(beanName);
        }
    }

    /**
     * Reset all bean definition caches for the given bean,
     * including the caches of beans that are derived from it.
     * @param beanName the name of the bean to reset
     */
    protected void resetBeanDefinition(String beanName) {
        // Remove the merged bean definition for the given bean, if already created.
        clearMergedBeanDefinition(beanName);

        // Remove corresponding bean from singleton cache, if any. Shouldn't usually
        // be necessary, rather just meant for overriding a context's default beans
        // (e.g. the default StaticMessageSource in a StaticApplicationContext).
        synchronized (getSingletonMutex()) {
            destroySingleton(beanName);
        }

        // Reset all bean definitions that have the given bean as parent
        // (recursively).
        for (Iterator it = this.beanDefinitionNames.iterator(); it.hasNext();) {
            String bdName = (String) it.next();
            if (!beanName.equals(bdName)) {
                BeanDefinition bd = (BeanDefinition) this.beanDefinitionMap.get(bdName);
                if (beanName.equals(bd.getParentName())) {
                    resetBeanDefinition(bdName);
                }
            }
        }
    }

    /**
     * Only allows alias overriding if bean definition overriding is allowed.
     */
    protected boolean allowAliasOverriding() {
        return this.allowBeanDefinitionOverriding;
    }


    //---------------------------------------------------------------------
    // Implementation of superclass abstract methods
    //---------------------------------------------------------------------

    public Object resolveDependency(DependencyDescriptor descriptor, String beanName,
                                    Set autowiredBeanNames, TypeConverter typeConverter) throws BeansException {

        Class type = descriptor.getDependencyType();
        if (type.isArray()) {
            Class componentType = type.getComponentType();
            Map matchingBeans = findAutowireCandidates(beanName, componentType, descriptor);
            if (matchingBeans.isEmpty()) {
                if (descriptor.isRequired()) {
                    raiseNoSuchBeanDefinitionException(componentType, "array of " + componentType.getName(), descriptor);
                }
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
            return converter.convertIfNecessary(matchingBeans.values(), type);
        }
        else if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
            Class elementType = descriptor.getCollectionType();
            if (elementType == null) {
                if (descriptor.isRequired()) {
                    throw new FatalBeanException("No element type declared for collection [" + type.getName() + "]");
                }
                return null;
            }
            Map matchingBeans = findAutowireCandidates(beanName, elementType, descriptor);
            if (matchingBeans.isEmpty()) {
                if (descriptor.isRequired()) {
                    raiseNoSuchBeanDefinitionException(elementType, "collection of " + elementType.getName(), descriptor);
                }
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
            return converter.convertIfNecessary(matchingBeans.values(), type);
        }
        else if (Map.class.isAssignableFrom(type) && type.isInterface()) {
            Class keyType = descriptor.getMapKeyType();
            if (keyType == null || !String.class.isAssignableFrom(keyType)) {
                if (descriptor.isRequired()) {
                    throw new FatalBeanException("Key type [" + keyType + "] of map [" + type.getName() +
                            "] must be assignable to [java.lang.String]");
                }
                return null;
            }
            Class valueType = descriptor.getMapValueType();
            if (valueType == null) {
                if (descriptor.isRequired()) {
                    throw new FatalBeanException("No value type declared for map [" + type.getName() + "]");
                }
                return null;
            }
            Map matchingBeans = findAutowireCandidates(beanName, valueType, descriptor);
            if (matchingBeans.isEmpty()) {
                if (descriptor.isRequired()) {
                    raiseNoSuchBeanDefinitionException(valueType, "map with value type " + valueType.getName(), descriptor);
                }
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            return matchingBeans;
        }
        else {
            Map matchingBeans = findAutowireCandidates(beanName, type, descriptor);
            if (matchingBeans.isEmpty()) {
                if (descriptor.isRequired()) {
                    throw new NoSuchBeanDefinitionException(type,
                            "Unsatisfied dependency of type [" + type + "]: expected at least 1 matching bean");
                }
                return null;
            }
            if (matchingBeans.size() > 1) {
                String primaryBeanName = determinePrimaryCandidate(matchingBeans, type);
                if (primaryBeanName == null) {
                    throw new NoSuchBeanDefinitionException(type,
                            "expected single matching bean but found " + matchingBeans.size() + ": " + matchingBeans.keySet());
                }
                if (autowiredBeanNames != null) {
                    autowiredBeanNames.add(primaryBeanName);
                }
                return matchingBeans.get(primaryBeanName);
            }
            // We have exactly one match.
            Map.Entry entry = (Map.Entry) matchingBeans.entrySet().iterator().next();
            if (autowiredBeanNames != null) {
                autowiredBeanNames.add(entry.getKey());
            }
            return entry.getValue();
        }
    }

    /**
     * Find bean instances that match the required type.
     * Called during autowiring for the specified bean.
     * @param beanName the name of the bean that is about to be wired
     * @param requiredType the actual type of bean to look for
     * (may be an array component type or collection element type)
     * @param descriptor the descriptor of the dependency to resolve
     * @return a Map of candidate names and candidate instances that match
     * the required type (never <code>null</code>)
     * @throws BeansException in case of errors
     * @see #autowireByType
     * @see #autowireConstructor
     */
    protected Map findAutowireCandidates(String beanName, Class requiredType, DependencyDescriptor descriptor) {
        String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                this, requiredType, true, descriptor.isEager());
        Map result = new LinkedHashMap(candidateNames.length);
        for (Iterator it = this.resolvableDependencies.keySet().iterator(); it.hasNext();) {
            Class autowiringType = (Class) it.next();
            if (autowiringType.isAssignableFrom(requiredType)) {
                Object autowiringValue = this.resolvableDependencies.get(autowiringType);
                if (autowiringValue instanceof ObjectFactory && !requiredType.isInstance(autowiringValue)) {
                    autowiringValue = ((ObjectFactory) autowiringValue).getObject();
                }
                if (requiredType.isInstance(autowiringValue)) {
                    result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
                    break;
                }
            }
        }
        for (int i = 0; i < candidateNames.length; i++) {
            String candidateName = candidateNames[i];
            if (!candidateName.equals(beanName) && isAutowireCandidate(candidateName, descriptor)) {
                result.put(candidateName, getBean(candidateName));
            }
        }
        return result;
    }

    /**
     * Determine the primary autowire candidate in the given set of beans.
     * @param candidateBeans a Map of candidate names and candidate instances
     * that match the required type, as returned by {@link #findAutowireCandidates}
     * @param type the required type
     * @return the name of the primary candidate, or <code>null</code> if none found
     */
    protected String determinePrimaryCandidate(Map candidateBeans, Class type) {
        String primaryBeanName = null;
        for (Iterator it = candidateBeans.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String candidateBeanName = (String) entry.getKey();
            if (isPrimary(candidateBeanName, entry.getValue())) {
                if (primaryBeanName != null) {
                    throw new NoSuchBeanDefinitionException(type,
                            "more than one 'primary' bean found among candidates: " + candidateBeans.keySet());
                }
                primaryBeanName = candidateBeanName;
            }
        }
        return primaryBeanName;
    }

    /**
     * Return whether the bean definition for the given bean name has been
     * marked as a primary bean.
     * @param beanName the name of the bean
     * @param beanInstance the corresponding bean instance
     * @return whether the given bean qualifies as primary
     */
    protected boolean isPrimary(String beanName, Object beanInstance) {
        if (containsBeanDefinition(beanName)) {
            return getMergedLocalBeanDefinition(beanName).isPrimary();
        }
        if (this.resolvableDependencies.values().contains(beanInstance)) {
            return true;
        }
        BeanFactory parentFactory = getParentBeanFactory();
        return (parentFactory instanceof DefaultListableBeanFactory &&
                ((DefaultListableBeanFactory) parentFactory).isPrimary(beanName, beanInstance));
    }

    /**
     * Raise a NoSuchBeanDefinitionException for an unresolvable dependency.
     */
    private void raiseNoSuchBeanDefinitionException(
            Class type, String dependencyDescription, DependencyDescriptor descriptor)
            throws NoSuchBeanDefinitionException {

        throw new NoSuchBeanDefinitionException(type, dependencyDescription,
                "expected at least 1 bean which qualifies as autowire candidate for this dependency. " +
                        "Dependency annotations: " + ObjectUtils.nullSafeToString(descriptor.getAnnotations()));
    }


    public String toString() {
        StringBuffer sb = new StringBuffer(ObjectUtils.identityToString(this));
        sb.append(": defining beans [");
        sb.append(StringUtils.arrayToCommaDelimitedString(getBeanDefinitionNames()));
        sb.append("]; ");
        BeanFactory parent = getParentBeanFactory();
        if (parent == null) {
            sb.append("root of factory hierarchy");
        }
        else {
            sb.append("parent: " + ObjectUtils.identityToString(parent));
        }
        return sb.toString();
    }

    /**
     * Set whether it should be allowed to override bean definitions by registering
     * a different definition with the same name, automatically replacing the former.
     * If not, an exception will be thrown. This also applies to overriding aliases.
     * <p>Default is "true".
     * @see #registerBeanDefinition
     */
    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    /**
     * Set whether the factory is allowed to eagerly load bean classes
     * even for bean definitions that are marked as "lazy-init".
     * <p>Default is "true". Turn this flag off to suppress class loading
     * for lazy-init beans unless such a bean is explicitly requested.
     * In particular, by-type lookups will then simply ignore bean definitions
     * without resolved class name, instead of loading the bean classes on
     * demand just to perform a type check.
     * @see AbstractBeanDefinition#setLazyInit
     */
    public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
        this.allowEagerClassLoading = allowEagerClassLoading;
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        return null;
    }
}
