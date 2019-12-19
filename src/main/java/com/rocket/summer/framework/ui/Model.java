package com.rocket.summer.framework.ui;

import java.util.Collection;
import java.util.Map;

/**
 * Java-5-specific interface that defines a holder for model attributes.
 * Primarily designed for adding attributes to the model.
 * Allows for accessing the overall model as a {@code java.util.Map}.
 *
 * @author Juergen Hoeller
 * @since 2.5.1
 */
public interface Model {

    /**
     * Add the supplied attribute under the supplied name.
     * @param attributeName the name of the model attribute (never {@code null})
     * @param attributeValue the model attribute value (can be {@code null})
     */
    Model addAttribute(String attributeName, Object attributeValue);

    /**
     * Add the supplied attribute to this {@code Map} using a
     * {@link com.rocket.summer.framework.core.Conventions#getVariableName generated name}.
     * <p><emphasis>Note: Empty {@link java.util.Collection Collections} are not added to
     * the model when using this method because we cannot correctly determine
     * the true convention name. View code should check for {@code null} rather
     * than for empty collections as is already done by JSTL tags.</emphasis>
     * @param attributeValue the model attribute value (never {@code null})
     */
    Model addAttribute(Object attributeValue);

    /**
     * Copy all attributes in the supplied {@code Collection} into this
     * {@code Map}, using attribute name generation for each element.
     * @see #addAttribute(Object)
     */
    Model addAllAttributes(Collection<?> attributeValues);

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map}.
     * @see #addAttribute(String, Object)
     */
    Model addAllAttributes(Map<String, ?> attributes);

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map},
     * with existing objects of the same name taking precedence (i.e. not getting
     * replaced).
     */
    Model mergeAttributes(Map<String, ?> attributes);

    /**
     * Does this model contain an attribute of the given name?
     * @param attributeName the name of the model attribute (never {@code null})
     * @return whether this model contains a corresponding attribute
     */
    boolean containsAttribute(String attributeName);

    /**
     * Return the current set of model attributes as a Map.
     */
    Map<String, Object> asMap();

}
