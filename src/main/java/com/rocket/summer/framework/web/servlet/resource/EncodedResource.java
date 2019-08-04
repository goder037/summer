package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

/**
 * Interface for a resource descriptor that describes the encoding
 * applied to the entire resource content.
 *
 * <p>This information is required if the client consuming that resource
 * needs additional decoding capabilities to retrieve the resource's content.
 *
 * @author Jeremy Grelle
 * @since 4.1
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.2.2">HTTP/1.1: Semantics
 * and Content, section 3.1.2.2</a>
 */
public interface EncodedResource extends Resource {

    /**
     * The content coding value, as defined in the IANA registry
     * @return the content encoding
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.2.1">HTTP/1.1: Semantics
     * and Content, section 3.1.2.1</a>
     */
    String getContentEncoding();

}
