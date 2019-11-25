package com.rocket.summer.framework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import com.rocket.summer.framework.data.domain.Pageable;
import com.rocket.summer.framework.data.repository.Repository;
import com.rocket.summer.framework.data.repository.core.CrudMethods;
import com.rocket.summer.framework.data.repository.core.RepositoryMetadata;
import com.rocket.summer.framework.data.repository.util.QueryExecutionConverters;
import com.rocket.summer.framework.data.util.ClassTypeInformation;
import com.rocket.summer.framework.data.util.ReflectionUtils;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.Assert;

/**
 * Base class for {@link RepositoryMetadata} implementations.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jens Schauder
 */
public abstract class AbstractRepositoryMetadata implements RepositoryMetadata {

    private final TypeInformation<?> typeInformation;
    private final Class<?> repositoryInterface;
    private CrudMethods crudMethods;

    /**
     * Creates a new {@link AbstractRepositoryMetadata}.
     *
     * @param repositoryInterface must not be {@literal null} and must be an interface.
     */
    public AbstractRepositoryMetadata(Class<?> repositoryInterface) {

        Assert.notNull(repositoryInterface, "Given type must not be null!");
        Assert.isTrue(repositoryInterface.isInterface(), "Given type must be an interface!");

        this.repositoryInterface = repositoryInterface;
        this.typeInformation = ClassTypeInformation.from(repositoryInterface);
    }

    /**
     * Creates a new {@link RepositoryMetadata} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     * @since 1.9
     * @return
     */
    public static RepositoryMetadata getMetadata(Class<?> repositoryInterface) {

        Assert.notNull(repositoryInterface, "Repository interface must not be null!");

        return Repository.class.isAssignableFrom(repositoryInterface) ? new DefaultRepositoryMetadata(repositoryInterface)
                : new AnnotationRepositoryMetadata(repositoryInterface);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.RepositoryMetadata#getReturnedDomainClass(java.lang.reflect.Method)
     */
    public Class<?> getReturnedDomainClass(Method method) {
        return QueryExecutionConverters.unwrapWrapperTypes(typeInformation.getReturnType(method)).getType();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.RepositoryMetadata#getRepositoryInterface()
     */
    public Class<?> getRepositoryInterface() {
        return this.repositoryInterface;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.RepositoryMetadata#getCrudMethods()
     */
    @Override
    public CrudMethods getCrudMethods() {

        if (this.crudMethods == null) {
            this.crudMethods = new DefaultCrudMethods(this);
        }

        return this.crudMethods;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.RepositoryMetadata#isPagingRepository()
     */
    @Override
    public boolean isPagingRepository() {

        Method findAllMethod = getCrudMethods().getFindAllMethod();

        if (findAllMethod == null) {
            return false;
        }

        return Arrays.asList(findAllMethod.getParameterTypes()).contains(Pageable.class);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.RepositoryMetadata#getAlternativeDomainTypes()
     */
    @Override
    public Set<Class<?>> getAlternativeDomainTypes() {
        return Collections.emptySet();
    }

    /**
     * Recursively unwraps well known wrapper types from the given {@link TypeInformation}.
     *
     * @param type must not be {@literal null}.
     * @return
     */
    private static Class<?> unwrapWrapperTypes(TypeInformation<?> type) {

        Class<?> rawType = type.getType();

        boolean needToUnwrap = Iterable.class.isAssignableFrom(rawType) || rawType.isArray()
                || QueryExecutionConverters.supports(rawType) || ReflectionUtils.isJava8StreamType(rawType);

        return needToUnwrap ? unwrapWrapperTypes(type.getComponentType()) : rawType;
    }
}

