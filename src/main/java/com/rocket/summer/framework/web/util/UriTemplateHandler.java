package com.rocket.summer.framework.web.util;

import java.net.URI;
import java.util.Map;

/**
 * Strategy for expanding a URI template with full control over the URI template
 * syntax and the encoding of variables. Also a convenient central point for
 * pre-processing all URI templates for example to insert a common base path.
 *
 * <p>Supported as a property on the {@code RestTemplate} as well as the
 * {@code AsyncRestTemplate}. The {@link DefaultUriTemplateHandler} is built
 * on Spring's URI template support via {@link UriComponentsBuilder}. An
 * alternative implementation may be used to plug external URI template libraries.
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 * @see com.rocket.summer.framework.web.client.RestTemplate#setUriTemplateHandler
 */
public interface UriTemplateHandler {

    /**
     * Expand the given URI template from a map of URI variables.
     * @param uriTemplate the URI template string
     * @param uriVariables the URI variables
     * @return the resulting URI
     */
    URI expand(String uriTemplate, Map<String, ?> uriVariables);

    /**
     * Expand the given URI template from an array of URI variables.
     * @param uriTemplate the URI template string
     * @param uriVariables the URI variable values
     * @return the resulting URI
     */
    URI expand(String uriTemplate, Object... uriVariables);

}

