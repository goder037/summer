package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.core.io.Resource;

/**
 * Simple implementation of {@link SourceExtractor} that returns <code>null</code>
 * as the source metadata.
 *
 * <p>This is the default implementation and prevents too much metadata from being
 * held in memory during normal (non-tooled) runtime usage.
 *
 * @author Rob Harrop
 * @since 2.0
 */
public class NullSourceExtractor implements SourceExtractor {

    /**
     * This implementation simply returns <code>null</code> for any input.
     */
    public Object extractSource(Object sourceCandidate, Resource definitionResource) {
        return null;
    }

}