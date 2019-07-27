package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.core.io.Resource;

/**
 * Simple strategy allowing tools to control how source metadata is attached
 * to the bean definition metadata.
 *
 * <p>Configuration parsers <strong>may</strong> provide the ability to attach
 * source metadata during the parse phase. They will offer this metadata in a
 * generic format which can be further modified by a {@link SourceExtractor}
 * before being attached to the bean definition metadata.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.beans.BeanMetadataElement#getSource()
 * @see com.rocket.summer.framework.beans.factory.config.BeanDefinition
 */
public interface SourceExtractor {

    /**
     * Extract the source metadata from the candidate object supplied
     * by the configuration parser.
     * @param sourceCandidate the original source metadata (never <code>null</code>)
     * @param definingResource the resource that defines the given source object
     * (may be <code>null</code>)
     * @return the source metadata object to store (may be <code>null</code>)
     */
    Object extractSource(Object sourceCandidate, Resource definingResource);

}

