package com.rocket.summer.framework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A strategy interface for retrieving and saving FlashMap instances.
 * See {@link FlashMap} for a general overview of flash attributes.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 *
 * @see FlashMap
 */
public interface FlashMapManager {

    /**
     * Find a FlashMap saved by a previous request that matches to the current
     * request, remove it from underlying storage, and also remove other
     * expired FlashMap instances.
     * <p>This method is invoked in the beginning of every request in contrast
     * to {@link #saveOutputFlashMap}, which is invoked only when there are
     * flash attributes to be saved - i.e. before a redirect.
     * @param request the current request
     * @param response the current response
     * @return a FlashMap matching the current request or {@code null}
     */
    FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response);

    /**
     * Save the given FlashMap, in some underlying storage and set the start
     * of its expiration period.
     * <p><strong>Note:</strong> Invoke this method prior to a redirect in order
     * to allow saving the FlashMap in the HTTP session or in a response
     * cookie before the response is committed.
     * @param flashMap the FlashMap to save
     * @param request the current request
     * @param response the current response
     */
    void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response);

}
