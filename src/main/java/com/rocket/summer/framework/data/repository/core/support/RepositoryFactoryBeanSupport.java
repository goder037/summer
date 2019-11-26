package com.rocket.summer.framework.data.repository.core.support;

import java.io.Serializable;
import java.util.List;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.context.ApplicationEventPublisher;
import com.rocket.summer.framework.context.ApplicationEventPublisherAware;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.context.MappingContext;
import com.rocket.summer.framework.data.repository.Repository;
import com.rocket.summer.framework.data.repository.core.EntityInformation;
import com.rocket.summer.framework.data.repository.core.NamedQueries;
import com.rocket.summer.framework.data.repository.core.RepositoryInformation;
import com.rocket.summer.framework.data.repository.core.RepositoryMetadata;
import com.rocket.summer.framework.data.repository.query.DefaultEvaluationContextProvider;
import com.rocket.summer.framework.data.repository.query.EvaluationContextProvider;
import com.rocket.summer.framework.data.repository.query.QueryLookupStrategy;
import com.rocket.summer.framework.data.repository.query.QueryLookupStrategy.Key;
import com.rocket.summer.framework.data.repository.query.QueryMethod;
import com.rocket.summer.framework.util.Assert;

/**
 * Adapter for Springs {@link FactoryBean} interface to allow easy setup of repository factories via Spring
 * configuration.
 *
 * @param <T> the type of the repository
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public abstract class RepositoryFactoryBeanSupport<T extends Repository<S, ID>, S, ID extends Serializable>
        implements InitializingBean, RepositoryFactoryInformation<S, ID>, FactoryBean<T>, BeanClassLoaderAware,
        BeanFactoryAware, ApplicationEventPublisherAware {

    private final Class<? extends T> repositoryInterface;

    private RepositoryFactorySupport factory;
    private Key queryLookupStrategyKey;
    private Class<?> repositoryBaseClass;
    private Object customImplementation;
    private NamedQueries namedQueries;
    private MappingContext<?, ?> mappingContext;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private boolean lazyInit = false;
    private EvaluationContextProvider evaluationContextProvider = DefaultEvaluationContextProvider.INSTANCE;
    private ApplicationEventPublisher publisher;

    private T repository;

    private RepositoryMetadata repositoryMetadata;

    /**
     * Creates a new {@link RepositoryFactoryBeanSupport} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    protected RepositoryFactoryBeanSupport(Class<? extends T> repositoryInterface) {

        Assert.notNull(repositoryInterface, "Repository interface must not be null!");
        this.repositoryInterface = repositoryInterface;
    }

    /**
     * Configures the repository base class to be used.
     *
     * @param repositoryBaseClass the repositoryBaseClass to set, can be {@literal null}.
     * @since 1.11
     */
    public void setRepositoryBaseClass(Class<?> repositoryBaseClass) {
        this.repositoryBaseClass = repositoryBaseClass;
    }

    /**
     * Set the {@link QueryLookupStrategy.Key} to be used.
     *
     * @param queryLookupStrategyKey
     */
    public void setQueryLookupStrategyKey(Key queryLookupStrategyKey) {
        this.queryLookupStrategyKey = queryLookupStrategyKey;
    }

    /**
     * Setter to inject a custom repository implementation.
     *
     * @param customImplementation
     */
    public void setCustomImplementation(Object customImplementation) {
        this.customImplementation = customImplementation;
    }

    /**
     * Setter to inject a {@link NamedQueries} instance.
     *
     * @param namedQueries the namedQueries to set
     */
    public void setNamedQueries(NamedQueries namedQueries) {
        this.namedQueries = namedQueries;
    }

    /**
     * Configures the {@link MappingContext} to be used to lookup {@link PersistentEntity} instances for
     * {@link #getPersistentEntity()}.
     *
     * @param mappingContext
     */
    protected void setMappingContext(MappingContext<?, ?> mappingContext) {
        this.mappingContext = mappingContext;
    }

    /**
     * Sets the {@link EvaluationContextProvider} to be used to evaluate SpEL expressions in manually defined queries.
     *
     * @param evaluationContextProvider can be {@literal null}, defaults to
     *          {@link DefaultEvaluationContextProvider#INSTANCE}.
     */
    public void setEvaluationContextProvider(EvaluationContextProvider evaluationContextProvider) {
        this.evaluationContextProvider = evaluationContextProvider == null ? DefaultEvaluationContextProvider.INSTANCE
                : evaluationContextProvider;
    }

    /**
     * Configures whether to initialize the repository proxy lazily. This defaults to {@literal false}.
     *
     * @param lazy whether to initialize the repository proxy lazily. This defaults to {@literal false}.
     */
    public void setLazyInit(boolean lazy) {
        this.lazyInit = lazy;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.BeanFactoryAware#setBeanFactory(com.rocket.summer.framework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(com.rocket.summer.framework.context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.support.RepositoryFactoryInformation#getEntityInformation()
     */
    @SuppressWarnings("unchecked")
    public EntityInformation<S, ID> getEntityInformation() {

        return (EntityInformation<S, ID>) factory.getEntityInformation(repositoryMetadata.getDomainType());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.support.RepositoryFactoryInformation#getRepositoryInformation()
     */
    public RepositoryInformation getRepositoryInformation() {

        return this.factory.getRepositoryInformation(repositoryMetadata,
                customImplementation == null ? null : customImplementation.getClass());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.support.RepositoryFactoryInformation#getPersistentEntity()
     */
    public PersistentEntity<?, ?> getPersistentEntity() {

        if (mappingContext == null) {
            return null;
        }

        return mappingContext.getPersistentEntity(repositoryMetadata.getDomainType());
    }

    /* (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.support.RepositoryFactoryInformation#getQueryMethods()
     */
    public List<QueryMethod> getQueryMethods() {
        return factory.getQueryMethods();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.FactoryBean#getObject()
     */
    public T getObject() {
        return initAndReturn();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<? extends T> getObjectType() {
        return repositoryInterface;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {

        this.factory = createRepositoryFactory();
        this.factory.setQueryLookupStrategyKey(queryLookupStrategyKey);
        this.factory.setNamedQueries(namedQueries);
        this.factory.setEvaluationContextProvider(evaluationContextProvider);
        this.factory.setRepositoryBaseClass(repositoryBaseClass);
        this.factory.setBeanClassLoader(classLoader);
        this.factory.setBeanFactory(beanFactory);

        if (publisher != null) {
            this.factory.addRepositoryProxyPostProcessor(new EventPublishingRepositoryProxyPostProcessor(publisher));
        }

        this.repositoryMetadata = this.factory.getRepositoryMetadata(repositoryInterface);

        if (!lazyInit) {
            initAndReturn();
        }
    }

    /**
     * Returns the previously initialized repository proxy or creates and returns the proxy if previously uninitialized.
     *
     * @return
     */
    private T initAndReturn() {

        Assert.notNull(repositoryInterface, "Repository interface must not be null on initialization!");

        if (this.repository == null) {
            this.repository = this.factory.getRepository(repositoryInterface, customImplementation);
        }

        return this.repository;
    }

    /**
     * Create the actual {@link RepositoryFactorySupport} instance.
     *
     * @return
     */
    protected abstract RepositoryFactorySupport createRepositoryFactory();
}

