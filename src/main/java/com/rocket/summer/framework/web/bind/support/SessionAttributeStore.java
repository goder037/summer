package com.rocket.summer.framework.web.bind.support;

import com.rocket.summer.framework.web.context.request.WebRequest;

/**
 * Strategy interface for storing model attributes in a backend session.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see com.rocket.summer.framework.web.bind.annotation.SessionAttributes
 */
public interface SessionAttributeStore {

    /**
     * Store the supplied attribute in the backend session.
     * <p>Can be called for new attributes as well as for existing attributes.
     * In the latter case, this signals that the attribute value may have been modified.
     * @param request the current request
     * @param attributeName the name of the attribute
     * @param attributeValue the attribute value to store
     */
    void storeAttribute(WebRequest request, String attributeName, Object attributeValue);

    /**
     * Retrieve the specified attribute from the backend session.
     * <p>This will typically be called with the expectation that the
     * attribute is already present, with an exception to be thrown
     * if this method returns <code>null</code>.
     * @param request the current request
     * @param attributeName the name of the attribute
     * @return the current attribute value, or <code>null</code> if none
     */
    Object retrieveAttribute(WebRequest request, String attributeName);

    /**
     * Clean up the specified attribute in the backend session.
     * <p>Indicates that the attribute name will not be used anymore.
     * @param request the current request
     * @param attributeName the name of the attribute
     */
    void cleanupAttribute(WebRequest request, String attributeName);

}
