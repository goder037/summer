package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.core.io.Resource;

/**
 * Simple {@link SourceExtractor} implementation that just passes
 * the candidate source metadata object through for attachment.
 *
 * <p>Using this implementation means that tools will get raw access to the
 * underlying configuration source metadata provided by the tool.
 *
 * <p>This implementation <strong>should not</strong> be used in a production
 * application since it is likely to keep too much metadata in memory
 * (unnecessarily).
 *
 * @author Rob Harrop
 * @since 2.0
 */
public class PassThroughSourceExtractor implements SourceExtractor {

    /**
     * Simply returns the supplied <code>sourceCandidate</code> as-is.
     * @param sourceCandidate the source metadata
     * @return the supplied <code>sourceCandidate</code>
     */
    public Object extractSource(Object sourceCandidate, Resource definingResource) {
        return sourceCandidate;
    }

}

