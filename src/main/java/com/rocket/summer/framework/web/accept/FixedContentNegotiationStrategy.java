package com.rocket.summer.framework.web.accept;

import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;

/**
 * A {@code ContentNegotiationStrategy} that returns a fixed content type.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class FixedContentNegotiationStrategy implements ContentNegotiationStrategy {

    private static final Log logger = LogFactory.getLog(FixedContentNegotiationStrategy.class);

    private final List<MediaType> contentType;


    /**
     * Create an instance with the given content type.
     */
    public FixedContentNegotiationStrategy(MediaType contentType) {
        this.contentType = Collections.singletonList(contentType);
    }


    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("Requested media types: " + this.contentType);
        }
        return this.contentType;
    }

}

