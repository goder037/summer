package com.rocket.summer.framework.web.method.annotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.bind.annotation.SessionAttributes;
import com.rocket.summer.framework.web.bind.support.SessionAttributeStore;
import com.rocket.summer.framework.web.bind.support.SessionStatus;
import com.rocket.summer.framework.web.context.request.WebRequest;

/**
 * Manages controller-specific session attributes declared via
 * {@link SessionAttributes @SessionAttributes}. Actual storage is
 * delegated to a {@link SessionAttributeStore} instance.
 *
 * <p>When a controller annotated with {@code @SessionAttributes} adds
 * attributes to its model, those attributes are checked against names and
 * types specified via {@code @SessionAttributes}. Matching model attributes
 * are saved in the HTTP session and remain there until the controller calls
 * {@link SessionStatus#setComplete()}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class SessionAttributesHandler {

    private final Set<String> attributeNames = new HashSet<String>();

    private final Set<Class<?>> attributeTypes = new HashSet<Class<?>>();

    private final Set<String> knownAttributeNames =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(4));

    private final SessionAttributeStore sessionAttributeStore;


    /**
     * Create a new session attributes handler. Session attribute names and types
     * are extracted from the {@code @SessionAttributes} annotation, if present,
     * on the given type.
     * @param handlerType the controller type
     * @param sessionAttributeStore used for session access
     */
    public SessionAttributesHandler(Class<?> handlerType, SessionAttributeStore sessionAttributeStore) {
        Assert.notNull(sessionAttributeStore, "SessionAttributeStore may not be null");
        this.sessionAttributeStore = sessionAttributeStore;

        SessionAttributes ann = AnnotatedElementUtils.findMergedAnnotation(handlerType, SessionAttributes.class);
        if (ann != null) {
            this.attributeNames.addAll(Arrays.asList(ann.names()));
            this.attributeTypes.addAll(Arrays.asList(ann.types()));
        }
        this.knownAttributeNames.addAll(this.attributeNames);
    }


    /**
     * Whether the controller represented by this instance has declared any
     * session attributes through an {@link SessionAttributes} annotation.
     */
    public boolean hasSessionAttributes() {
        return (!this.attributeNames.isEmpty() || !this.attributeTypes.isEmpty());
    }

    /**
     * Whether the attribute name or type match the names and types specified
     * via {@code @SessionAttributes} on the underlying controller.
     * <p>Attributes successfully resolved through this method are "remembered"
     * and subsequently used in {@link #retrieveAttributes(WebRequest)} and
     * {@link #cleanupAttributes(WebRequest)}.
     * @param attributeName the attribute name to check
     * @param attributeType the type for the attribute
     */
    public boolean isHandlerSessionAttribute(String attributeName, Class<?> attributeType) {
        Assert.notNull(attributeName, "Attribute name must not be null");
        if (this.attributeNames.contains(attributeName) || this.attributeTypes.contains(attributeType)) {
            this.knownAttributeNames.add(attributeName);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Store a subset of the given attributes in the session. Attributes not
     * declared as session attributes via {@code @SessionAttributes} are ignored.
     * @param request the current request
     * @param attributes candidate attributes for session storage
     */
    public void storeAttributes(WebRequest request, Map<String, ?> attributes) {
        for (String name : attributes.keySet()) {
            Object value = attributes.get(name);
            Class<?> attrType = (value != null ? value.getClass() : null);
            if (isHandlerSessionAttribute(name, attrType)) {
                this.sessionAttributeStore.storeAttribute(request, name, value);
            }
        }
    }

    /**
     * Retrieve "known" attributes from the session, i.e. attributes listed
     * by name in {@code @SessionAttributes} or attributes previously stored
     * in the model that matched by type.
     * @param request the current request
     * @return a map with handler session attributes, possibly empty
     */
    public Map<String, Object> retrieveAttributes(WebRequest request) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (String name : this.knownAttributeNames) {
            Object value = this.sessionAttributeStore.retrieveAttribute(request, name);
            if (value != null) {
                attributes.put(name, value);
            }
        }
        return attributes;
    }

    /**
     * Remove "known" attributes from the session, i.e. attributes listed
     * by name in {@code @SessionAttributes} or attributes previously stored
     * in the model that matched by type.
     * @param request the current request
     */
    public void cleanupAttributes(WebRequest request) {
        for (String attributeName : this.knownAttributeNames) {
            this.sessionAttributeStore.cleanupAttribute(request, attributeName);
        }
    }

    /**
     * A pass-through call to the underlying {@link SessionAttributeStore}.
     * @param request the current request
     * @param attributeName the name of the attribute of interest
     * @return the attribute value, or {@code null} if none
     */
    Object retrieveAttribute(WebRequest request, String attributeName) {
        return this.sessionAttributeStore.retrieveAttribute(request, attributeName);
    }

}

