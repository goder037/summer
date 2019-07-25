package com.rocket.summer.framework.web.bind.support;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.request.WebRequest;

/**
 * Default implementation of the {@link SessionAttributeStore} interface,
 * storing the attributes in the WebRequest session (i.e. HttpSession
 * or PortletSession).
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see #setAttributeNamePrefix
 * @see org.springframework.web.context.request.WebRequest#setAttribute
 * @see org.springframework.web.context.request.WebRequest#getAttribute
 * @see org.springframework.web.context.request.WebRequest#removeAttribute
 */
public class DefaultSessionAttributeStore implements SessionAttributeStore {

    private String attributeNamePrefix = "";


    /**
     * Specify a prefix to use for the attribute names in the backend session.
     * <p>Default is to use no prefix, storing the session attributes with the
     * same name as in the model.
     */
    public void setAttributeNamePrefix(String attributeNamePrefix) {
        this.attributeNamePrefix = (attributeNamePrefix != null ? attributeNamePrefix : "");
    }


    public void storeAttribute(WebRequest request, String attributeName, Object attributeValue) {
        Assert.notNull(request, "WebRequest must not be null");
        Assert.notNull(attributeName, "Attribute name must not be null");
        Assert.notNull(attributeValue, "Attribute value must not be null");
        String storeAttributeName = getAttributeNameInSession(request, attributeName);
        request.setAttribute(storeAttributeName, attributeValue, WebRequest.SCOPE_SESSION);
    }

    public Object retrieveAttribute(WebRequest request, String attributeName) {
        Assert.notNull(request, "WebRequest must not be null");
        Assert.notNull(attributeName, "Attribute name must not be null");
        String storeAttributeName = getAttributeNameInSession(request, attributeName);
        return request.getAttribute(storeAttributeName, WebRequest.SCOPE_SESSION);
    }

    public void cleanupAttribute(WebRequest request, String attributeName) {
        Assert.notNull(request, "WebRequest must not be null");
        Assert.notNull(attributeName, "Attribute name must not be null");
        String storeAttributeName = getAttributeNameInSession(request, attributeName);
        request.removeAttribute(storeAttributeName, WebRequest.SCOPE_SESSION);
    }


    /**
     * Calculate the attribute name in the backend session.
     * <p>The default implementation simply prepends the configured
     * {@link #setAttributeNamePrefix "attributeNamePrefix"}, if any.
     * @param request the current request
     * @param attributeName the name of the attribute
     * @return the attribute name in the backend session
     */
    protected String getAttributeNameInSession(WebRequest request, String attributeName) {
        return this.attributeNamePrefix + attributeName;
    }

}
