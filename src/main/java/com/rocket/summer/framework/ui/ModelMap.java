package com.rocket.summer.framework.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rocket.summer.framework.core.Conventions;
import com.rocket.summer.framework.util.Assert;

/**
 * Implementation of {@link java.util.Map} for use when building model data for use
 * with UI tools. Supports chained calls and generation of model attribute names.
 *
 * <p>This class serves as generic model holder for both Servlet and Portlet MVC,
 * but is not tied to either of those. Check out the {@link Model} interface for
 * a Java-5-based interface variant that serves the same purpose.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see Conventions#getVariableName
 * @see com.rocket.summer.framework.web.servlet.ModelAndView
 * @see com.rocket.summer.framework.web.portlet.ModelAndView
 */
@SuppressWarnings("serial")
public class ModelMap extends LinkedHashMap<String, Object> {

    /**
     * Construct a new, empty {@code ModelMap}.
     */
    public ModelMap() {
    }

    /**
     * Construct a new {@code ModelMap} containing the supplied attribute
     * under the supplied name.
     * @see #addAttribute(String, Object)
     */
    public ModelMap(String attributeName, Object attributeValue) {
        addAttribute(attributeName, attributeValue);
    }

    /**
     * Construct a new {@code ModelMap} containing the supplied attribute.
     * Uses attribute name generation to generate the key for the supplied model
     * object.
     * @see #addAttribute(Object)
     */
    public ModelMap(Object attributeValue) {
        addAttribute(attributeValue);
    }


    /**
     * Add the supplied attribute under the supplied name.
     * @param attributeName the name of the model attribute (never {@code null})
     * @param attributeValue the model attribute value (can be {@code null})
     */
    public ModelMap addAttribute(String attributeName, Object attributeValue) {
        Assert.notNull(attributeName, "Model attribute name must not be null");
        put(attributeName, attributeValue);
        return this;
    }

    /**
     * Add the supplied attribute to this {@code Map} using a
     * {@link com.rocket.summer.framework.core.Conventions#getVariableName generated name}.
     * <p><emphasis>Note: Empty {@link Collection Collections} are not added to
     * the model when using this method because we cannot correctly determine
     * the true convention name. View code should check for {@code null} rather
     * than for empty collections as is already done by JSTL tags.</emphasis>
     * @param attributeValue the model attribute value (never {@code null})
     */
    public ModelMap addAttribute(Object attributeValue) {
        Assert.notNull(attributeValue, "Model object must not be null");
        if (attributeValue instanceof Collection && ((Collection<?>) attributeValue).isEmpty()) {
            return this;
        }
        return addAttribute(Conventions.getVariableName(attributeValue), attributeValue);
    }

    /**
     * Copy all attributes in the supplied {@code Collection} into this
     * {@code Map}, using attribute name generation for each element.
     * @see #addAttribute(Object)
     */
    public ModelMap addAllAttributes(Collection<?> attributeValues) {
        if (attributeValues != null) {
            for (Object attributeValue : attributeValues) {
                addAttribute(attributeValue);
            }
        }
        return this;
    }

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map}.
     * @see #addAttribute(String, Object)
     */
    public ModelMap addAllAttributes(Map<String, ?> attributes) {
        if (attributes != null) {
            putAll(attributes);
        }
        return this;
    }

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map},
     * with existing objects of the same name taking precedence (i.e. not getting
     * replaced).
     */
    public ModelMap mergeAttributes(Map<String, ?> attributes) {
        if (attributes != null) {
            for (Map.Entry<String, ?> entry : attributes.entrySet()) {
                String key = entry.getKey();
                if (!containsKey(key)) {
                    put(key, entry.getValue());
                }
            }
        }
        return this;
    }

    /**
     * Does this model contain an attribute of the given name?
     * @param attributeName the name of the model attribute (never {@code null})
     * @return whether this model contains a corresponding attribute
     */
    public boolean containsAttribute(String attributeName) {
        return containsKey(attributeName);
    }

}
