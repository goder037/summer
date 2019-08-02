package com.rocket.summer.framework.core.type.filter;

import com.rocket.summer.framework.core.type.ClassMetadata;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * Type filter that exposes a
 * {@link com.rocket.summer.framework.core.type.ClassMetadata} object
 * to subclasses, for class testing purposes.
 *
 * @author Rod Johnson
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 2.5
 * @see #match(com.rocket.summer.framework.core.type.ClassMetadata)
 */
public abstract class AbstractClassTestingTypeFilter implements TypeFilter {

    @Override
    public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException {

        return match(metadataReader.getClassMetadata());
    }

    /**
     * Determine a match based on the given ClassMetadata object.
     * @param metadata the ClassMetadata object
     * @return whether this filter matches on the specified type
     */
    protected abstract boolean match(ClassMetadata metadata);

}
