package com.rocket.summer.framework.data.repository.core.support;

import java.io.Serializable;

import com.rocket.summer.framework.data.repository.RepositoryDefinition;
import com.rocket.summer.framework.data.repository.core.RepositoryMetadata;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link RepositoryMetadata} implementation inspecting the given repository interface for a
 * {@link RepositoryDefinition} annotation.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public class AnnotationRepositoryMetadata extends AbstractRepositoryMetadata {

    private static final String NO_ANNOTATION_FOUND = String.format("Interface must be annotated with @%s!",
            RepositoryDefinition.class.getName());

    private final Class<? extends Serializable> idType;
    private final Class<?> domainType;

    /**
     * Creates a new {@link AnnotationRepositoryMetadata} instance looking up repository types from a
     * {@link RepositoryDefinition} annotation.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public AnnotationRepositoryMetadata(Class<?> repositoryInterface) {

        super(repositoryInterface);
        Assert.isTrue(repositoryInterface.isAnnotationPresent(RepositoryDefinition.class), NO_ANNOTATION_FOUND);

        this.idType = resolveIdType(repositoryInterface);
        this.domainType = resolveDomainType(repositoryInterface);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.RepositoryMetadata#getIdType()
     */
    @Override
    public Class<? extends Serializable> getIdType() {
        return this.idType;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.core.RepositoryMetadata#getDomainType()
     */
    @Override
    public Class<?> getDomainType() {
        return this.domainType;
    }

    private Class<? extends Serializable> resolveIdType(Class<?> repositoryInterface) {

        RepositoryDefinition annotation = repositoryInterface.getAnnotation(RepositoryDefinition.class);

        if (annotation == null || annotation.idClass() == null) {
            throw new IllegalArgumentException(String.format("Could not resolve id type of %s!", repositoryInterface));
        }

        return annotation.idClass();
    }

    private Class<?> resolveDomainType(Class<?> repositoryInterface) {

        RepositoryDefinition annotation = repositoryInterface.getAnnotation(RepositoryDefinition.class);

        if (annotation == null || annotation.domainClass() == null) {
            throw new IllegalArgumentException(String.format("Could not resolve domain type of %s!", repositoryInterface));
        }

        return annotation.domainClass();
    }
}

