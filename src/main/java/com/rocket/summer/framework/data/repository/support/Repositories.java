package com.rocket.summer.framework.data.repository.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryUtils;
import com.rocket.summer.framework.beans.factory.ListableBeanFactory;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.context.MappingContext;
import com.rocket.summer.framework.data.repository.core.EntityInformation;
import com.rocket.summer.framework.data.repository.core.RepositoryInformation;
import com.rocket.summer.framework.data.repository.core.support.RepositoryFactoryInformation;
import com.rocket.summer.framework.data.repository.query.QueryMethod;
import com.rocket.summer.framework.data.util.ProxyUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Wrapper class to access repository instances obtained from a {@link ListableBeanFactory}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Thomas Eizinger
 */
public class Repositories implements Iterable<Class<?>> {

    static final Repositories NONE = new Repositories();

    private static final RepositoryFactoryInformation<Object, Serializable> EMPTY_REPOSITORY_FACTORY_INFO = EmptyRepositoryFactoryInformation.INSTANCE;
    private static final String DOMAIN_TYPE_MUST_NOT_BE_NULL = "Domain type must not be null!";

    private final BeanFactory beanFactory;
    private final Map<Class<?>, String> repositoryBeanNames;
    private final Map<Class<?>, RepositoryFactoryInformation<Object, Serializable>> repositoryFactoryInfos;

    /**
     * Constructor to create the {@link #NONE} instance.
     */
    private Repositories() {

        this.beanFactory = null;
        this.repositoryBeanNames = Collections.<Class<?>, String> emptyMap();
        this.repositoryFactoryInfos = Collections.<Class<?>, RepositoryFactoryInformation<Object, Serializable>> emptyMap();
    }

    /**
     * Creates a new {@link Repositories} instance by looking up the repository instances and meta information from the
     * given {@link ListableBeanFactory}.
     *
     * @param factory must not be {@literal null}.
     */
    public Repositories(ListableBeanFactory factory) {

        Assert.notNull(factory, "Factory must not be null!");

        this.beanFactory = factory;
        this.repositoryFactoryInfos = new HashMap<Class<?>, RepositoryFactoryInformation<Object, Serializable>>();
        this.repositoryBeanNames = new HashMap<Class<?>, String>();

        populateRepositoryFactoryInformation(factory);
    }

    private void populateRepositoryFactoryInformation(ListableBeanFactory factory) {

        for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(factory, RepositoryFactoryInformation.class,
                false, false)) {
            cacheRepositoryFactory(name);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private synchronized void cacheRepositoryFactory(String name) {

        RepositoryFactoryInformation repositoryFactoryInformation = beanFactory.getBean(name,
                RepositoryFactoryInformation.class);
        Class<?> domainType = ClassUtils
                .getUserClass(repositoryFactoryInformation.getRepositoryInformation().getDomainType());

        RepositoryInformation information = repositoryFactoryInformation.getRepositoryInformation();
        Set<Class<?>> alternativeDomainTypes = information.getAlternativeDomainTypes();
        String beanName = BeanFactoryUtils.transformedBeanName(name);

        Set<Class<?>> typesToRegister = new HashSet<Class<?>>(alternativeDomainTypes.size() + 1);
        typesToRegister.add(domainType);
        typesToRegister.addAll(alternativeDomainTypes);

        for (Class<?> type : typesToRegister) {
            this.repositoryFactoryInfos.put(type, repositoryFactoryInformation);
            this.repositoryBeanNames.put(type, beanName);
        }
    }

    /**
     * Returns whether we have a repository instance registered to manage instances of the given domain class.
     *
     * @param domainClass must not be {@literal null}.
     * @return
     */
    public boolean hasRepositoryFor(Class<?> domainClass) {

        Assert.notNull(domainClass, DOMAIN_TYPE_MUST_NOT_BE_NULL);

        Class<?> userClass = ProxyUtils.getUserClass(domainClass);

        return repositoryFactoryInfos.containsKey(userClass);
    }

    /**
     * Returns the repository managing the given domain class.
     *
     * @param domainClass must not be {@literal null}.
     * @return
     */
    public Object getRepositoryFor(Class<?> domainClass) {

        Assert.notNull(domainClass, DOMAIN_TYPE_MUST_NOT_BE_NULL);

        Class<?> userClass = ProxyUtils.getUserClass(domainClass);
        String repositoryBeanName = repositoryBeanNames.get(userClass);

        return repositoryBeanName == null || beanFactory == null ? null : beanFactory.getBean(repositoryBeanName);
    }

    /**
     * Returns the {@link RepositoryFactoryInformation} for the given domain class. The given <code>code</code> is
     * converted to the actual user class if necessary, @see ProxyUtils#getUserClass.
     *
     * @param domainClass must not be {@literal null}.
     * @return the {@link RepositoryFactoryInformation} for the given domain class or {@literal null} if no repository
     *         registered for this domain class.
     */
    private RepositoryFactoryInformation<Object, Serializable> getRepositoryFactoryInfoFor(Class<?> domainClass) {

        Assert.notNull(domainClass, DOMAIN_TYPE_MUST_NOT_BE_NULL);

        Class<?> userType = ProxyUtils.getUserClass(domainClass);
        RepositoryFactoryInformation<Object, Serializable> repositoryInfo = repositoryFactoryInfos.get(userType);

        if (repositoryInfo != null) {
            return repositoryInfo;
        }

        if (!userType.equals(Object.class)) {
            return getRepositoryFactoryInfoFor(userType.getSuperclass());
        }

        return EMPTY_REPOSITORY_FACTORY_INFO;
    }

    /**
     * Returns the {@link EntityInformation} for the given domain class.
     *
     * @param domainClass must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T, S extends Serializable> EntityInformation<T, S> getEntityInformationFor(Class<?> domainClass) {

        Assert.notNull(domainClass, DOMAIN_TYPE_MUST_NOT_BE_NULL);

        return (EntityInformation<T, S>) getRepositoryFactoryInfoFor(domainClass).getEntityInformation();
    }

    /**
     * Returns the {@link RepositoryInformation} for the given domain class.
     *
     * @param domainClass must not be {@literal null}.
     * @return the {@link RepositoryInformation} for the given domain class or {@literal null} if no repository registered
     *         for this domain class.
     */
    public RepositoryInformation getRepositoryInformationFor(Class<?> domainClass) {

        Assert.notNull(domainClass, DOMAIN_TYPE_MUST_NOT_BE_NULL);

        RepositoryFactoryInformation<Object, Serializable> information = getRepositoryFactoryInfoFor(domainClass);
        return information == EMPTY_REPOSITORY_FACTORY_INFO ? null : information.getRepositoryInformation();
    }

    /**
     * Returns the {@link RepositoryInformation} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     * @return the {@link RepositoryInformation} for the given repository interface or {@literal null} there's no
     *         repository instance registered for the given interface.
     * @since 1.12
     */
    public RepositoryInformation getRepositoryInformation(Class<?> repositoryInterface) {

        for (RepositoryFactoryInformation<Object, Serializable> factoryInformation : repositoryFactoryInfos.values()) {

            RepositoryInformation information = factoryInformation.getRepositoryInformation();

            if (information.getRepositoryInterface().equals(repositoryInterface)) {
                return information;
            }
        }

        return null;
    }

    /**
     * Returns the {@link PersistentEntity} for the given domain class. Might return {@literal null} in case the module
     * storing the given domain class does not support the mapping subsystem.
     *
     * @param domainClass must not be {@literal null}.
     * @return the {@link PersistentEntity} for the given domain class or {@literal null} if no repository is registered
     *         for the domain class or the repository is not backed by a {@link MappingContext} implementation.
     */
    public PersistentEntity<?, ?> getPersistentEntity(Class<?> domainClass) {

        Assert.notNull(domainClass, DOMAIN_TYPE_MUST_NOT_BE_NULL);
        return getRepositoryFactoryInfoFor(domainClass).getPersistentEntity();
    }

    /**
     * Returns the {@link QueryMethod}s contained in the repository managing the given domain class.
     *
     * @param domainClass must not be {@literal null}.
     * @return
     */
    public List<QueryMethod> getQueryMethodsFor(Class<?> domainClass) {

        Assert.notNull(domainClass, DOMAIN_TYPE_MUST_NOT_BE_NULL);
        return getRepositoryFactoryInfoFor(domainClass).getQueryMethods();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Class<?>> iterator() {
        return repositoryFactoryInfos.keySet().iterator();
    }

    /**
     * Null-object to avoid nasty {@literal null} checks in cache lookups.
     *
     * @author Thomas Darimont
     */
    private static enum EmptyRepositoryFactoryInformation implements RepositoryFactoryInformation<Object, Serializable> {

        INSTANCE;

        @Override
        public EntityInformation<Object, Serializable> getEntityInformation() {
            return null;
        }

        @Override
        public RepositoryInformation getRepositoryInformation() {
            return null;
        }

        @Override
        public PersistentEntity<?, ?> getPersistentEntity() {
            return null;
        }

        @Override
        public List<QueryMethod> getQueryMethods() {
            return Collections.<QueryMethod> emptyList();
        }
    }
}

