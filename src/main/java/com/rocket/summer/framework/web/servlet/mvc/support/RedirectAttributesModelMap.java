package com.rocket.summer.framework.web.servlet.mvc.support;

import com.rocket.summer.framework.validation.DataBinder;
import com.rocket.summer.framework.web.ui.ModelMap;

import java.util.Collection;
import java.util.Map;

/**
 * A {@link ModelMap} implementation of {@link RedirectAttributes} that formats
 * values as Strings using a {@link DataBinder}. Also provides a place to store
 * flash attributes so they can survive a redirect without the need to be
 * embedded in the redirect URL.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
@SuppressWarnings("serial")
public class RedirectAttributesModelMap extends ModelMap implements RedirectAttributes {

    private final DataBinder dataBinder;

    private final ModelMap flashAttributes = new ModelMap();

    /**
     * Class constructor.
     * @param dataBinder used to format attribute values as Strings.
     */
    public RedirectAttributesModelMap(DataBinder dataBinder) {
        this.dataBinder = dataBinder;
    }

    /**
     * Default constructor without a DataBinder.
     * Attribute values are converted to String via {@link #toString()}.
     */
    public RedirectAttributesModelMap() {
        this(null);
    }

    /**
     * Return the attributes candidate for flash storage or an empty Map.
     */
    public Map<String, ?> getFlashAttributes() {
        return this.flashAttributes;
    }

    /**
     * {@inheritDoc}
     * <p>Formats the attribute value as a String before adding it.
     */
    public RedirectAttributesModelMap addAttribute(String attributeName, Object attributeValue) {
        super.addAttribute(attributeName, formatValue(attributeValue));
        return this;
    }

    private String formatValue(Object value) {
        if (value == null) {
            return null;
        }
        return (dataBinder != null) ? dataBinder.convertIfNecessary(value, String.class) : value.toString();
    }

    /**
     * {@inheritDoc}
     * <p>Formats the attribute value as a String before adding it.
     */
    public RedirectAttributesModelMap addAttribute(Object attributeValue) {
        super.addAttribute(attributeValue);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>Each attribute value is formatted as a String before being added.
     */
    public RedirectAttributesModelMap addAllAttributes(Collection<?> attributeValues) {
        super.addAllAttributes(attributeValues);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>Each attribute value is formatted as a String before being added.
     */
    public RedirectAttributesModelMap addAllAttributes(Map<String, ?> attributes) {
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                addAttribute(key, attributes.get(key));
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>Each attribute value is formatted as a String before being merged.
     */
    public RedirectAttributesModelMap mergeAttributes(Map<String, ?> attributes) {
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                if (!containsKey(key)) {
                    addAttribute(key, attributes.get(key));
                }
            }
        }
        return this;
    }

    public Map<String, Object> asMap() {
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>The value is formatted as a String before being added.
     */
    @Override
    public Object put(String key, Object value) {
        return super.put(key, formatValue(value));
    }

    /**
     * {@inheritDoc}
     * <p>Each value is formatted as a String before being added.
     */
    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                put(key, formatValue(map.get(key)));
            }
        }
    }

    public RedirectAttributes addFlashAttribute(String attributeName, Object attributeValue) {
        this.flashAttributes.addAttribute(attributeName, attributeValue);
        return this;
    }

    public RedirectAttributes addFlashAttribute(Object attributeValue) {
        this.flashAttributes.addAttribute(attributeValue);
        return this;
    }

}

