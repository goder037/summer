package com.rocket.summer.framework.web.ui;

import java.util.Collection;
import java.util.Map;

/**
 * Subclass of {@link ModelMap} that implements the {@link Model} interface.
 * Java 5 specific like the <code>Model</code> interface itself.
 *
 * @author Juergen Hoeller
 * @since 2.5.1
 */
public class ExtendedModelMap extends ModelMap implements Model {

    @Override
    public ExtendedModelMap addAttribute(String attributeName, Object attributeValue) {
        super.addAttribute(attributeName, attributeValue);
        return this;
    }

    @Override
    public ExtendedModelMap addAttribute(Object attributeValue) {
        super.addAttribute(attributeValue);
        return this;
    }

    @Override
    public ExtendedModelMap addAllAttributes(Collection<?> attributeValues) {
        super.addAllAttributes(attributeValues);
        return this;
    }

    @Override
    public ExtendedModelMap addAllAttributes(Map<String, ?> attributes) {
        super.addAllAttributes(attributes);
        return this;
    }

    @Override
    public ExtendedModelMap mergeAttributes(Map<String, ?> attributes) {
        super.mergeAttributes(attributes);
        return this;
    }

    public Map<String, Object> asMap() {
        return this;
    }

}
