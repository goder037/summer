package com.rocket.summer.framework.cglib.core;

import com.rocket.summer.framework.asm.Type;

/**
 * Customizes key types for {@link KeyFactory} when building equals, hashCode, and toString.
 * For customization of field types, use {@link FieldTypeCustomizer}
 *
 * @see KeyFactory#CLASS_BY_NAME
 */
public interface Customizer extends KeyFactoryCustomizer {
    void customize(CodeEmitter e, Type type);
}
