package com.rocket.summer.framework.data.keyvalue.core;

import java.io.Serializable;

import com.rocket.summer.framework.data.mapping.IdentifierAccessor;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link IdentifierAccessor} adding a {@link #getOrGenerateIdentifier()} to automatically generate an identifier and
 * set it on the underling bean instance.
 *
 * @author Oliver Gierke
 * @see #getOrGenerateIdentifier()
 */
class GeneratingIdAccessor implements IdentifierAccessor {

    private final PersistentPropertyAccessor accessor;
    private final PersistentProperty<?> identifierProperty;
    private final IdentifierGenerator generator;

    /**
     * Creates a new {@link GeneratingIdAccessor} using the given {@link PersistentPropertyAccessor}, identifier property
     * and {@link IdentifierGenerator}.
     *
     * @param accessor must not be {@literal null}.
     * @param identifierProperty must not be {@literal null}.
     * @param generator must not be {@literal null}.
     */
    public GeneratingIdAccessor(PersistentPropertyAccessor accessor, PersistentProperty<?> identifierProperty,
                                IdentifierGenerator generator) {

        Assert.notNull(accessor, "PersistentPropertyAccessor must not be null!");
        Assert.notNull(identifierProperty, "Identifier property must not be null!");
        Assert.notNull(generator, "IdentifierGenerator must not be null!");

        this.accessor = accessor;
        this.identifierProperty = identifierProperty;
        this.generator = generator;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.IdentifierAccessor#getIdentifier()
     */
    @Override
    public Object getIdentifier() {
        return accessor.getProperty(identifierProperty);
    }

    /**
     * Returns the identifier value of the backing bean or generates a new one using the configured
     * {@link IdentifierGenerator}.
     *
     * @return
     */
    public Object getOrGenerateIdentifier() {

        Serializable existingIdentifier = (Serializable) getIdentifier();

        if (existingIdentifier != null) {
            return existingIdentifier;
        }

        Object generatedIdentifier = generator.generateIdentifierOfType(identifierProperty.getTypeInformation());
        accessor.setProperty(identifierProperty, generatedIdentifier);

        return generatedIdentifier;
    }
}

