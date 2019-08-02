package com.rocket.summer.framework.web.accept;

import com.rocket.summer.framework.http.MediaType;

import java.util.List;

/**
 * Strategy to resolve {@link MediaType} to a list of file extensions.
 * For example resolve "application/json" to "json".
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public interface MediaTypeFileExtensionResolver {

    /**
     * Resolve the given media type to a list of path extensions.
     * @param mediaType the media type to resolve
     * @return a list of extensions or an empty list (never {@code null})
     */
    List<String> resolveFileExtensions(MediaType mediaType);

    /**
     * Return all registered file extensions.
     * @return a list of extensions or an empty list (never {@code null})
     */
    List<String> getAllFileExtensions();

}

