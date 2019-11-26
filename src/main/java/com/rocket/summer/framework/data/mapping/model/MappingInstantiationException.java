package com.rocket.summer.framework.data.mapping.model;

import java.lang.reflect.Constructor;
import java.util.List;

import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PreferredConstructor;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Exception being thrown in case an entity could not be instantiated in the process of a to-object-mapping.
 *
 * @author Oliver Gierke
 * @author Jon Brisbin
 */
public class MappingInstantiationException extends RuntimeException {

    private static final long serialVersionUID = 822211065035487628L;
    private static final String TEXT_TEMPLATE = "Failed to instantiate %s using constructor %s with arguments %s";

    private final Class<?> entityType;
    private final Constructor<?> constructor;
    private final List<Object> constructorArguments;

    /**
     * Creates a new {@link MappingInstantiationException} for the given {@link PersistentEntity}, constructor arguments
     * and the causing exception.
     *
     * @param entity
     * @param arguments
     * @param cause
     */
    public MappingInstantiationException(PersistentEntity<?, ?> entity, List<Object> arguments, Exception cause) {
        this(entity, arguments, null, cause);
    }

    private MappingInstantiationException(PersistentEntity<?, ?> entity, List<Object> arguments, String message,
                                          Exception cause) {

        super(buildExceptionMessage(entity, arguments, message), cause);

        this.entityType = entity == null ? null : entity.getType();
        this.constructor = entity == null || entity.getPersistenceConstructor() == null ? null : entity
                .getPersistenceConstructor().getConstructor();
        this.constructorArguments = arguments;
    }

    private static final String buildExceptionMessage(PersistentEntity<?, ?> entity, List<Object> arguments,
                                                      String defaultMessage) {

        if (entity == null) {
            return defaultMessage;
        }

        PreferredConstructor<?, ?> constructor = entity.getPersistenceConstructor();

        return String.format(TEXT_TEMPLATE, entity.getType().getName(), constructor == null ? "NO_CONSTRUCTOR"
                : constructor.getConstructor().toString(), StringUtils.collectionToCommaDelimitedString(arguments));
    }

    /**
     * Returns the type of the entity that was attempted to instantiate.
     *
     * @return the entityType
     */
    public Class<?> getEntityType() {
        return entityType;
    }

    /**
     * The constructor used during the instantiation attempt.
     *
     * @return the constructor
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * The constructor arguments used to invoke the constructor.
     *
     * @return the constructorArguments
     */
    public List<Object> getConstructorArguments() {
        return constructorArguments;
    }
}

