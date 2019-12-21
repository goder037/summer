package com.rocket.summer.framework.expression;

/**
 * Implementers of this interface are expected to be able to locate types.
 * They may use a custom {@link ClassLoader} and/or deal with common
 * package prefixes (e.g. {@code java.lang}) however they wish.
 *
 * <p>See {@link com.rocket.summer.framework.expression.spel.support.StandardTypeLocator}
 * for an example implementation.
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface TypeLocator {

    /**
     * Find a type by name. The name may or may not be fully qualified
     * (e.g. {@code String} or {@code java.lang.String}).
     * @param typeName the type to be located
     * @return the {@code Class} object representing that type
     * @throws EvaluationException if there is a problem finding the type
     */
    Class<?> findType(String typeName) throws EvaluationException;

}