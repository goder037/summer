package com.rocket.summer.framework.ui;

import java.util.Collection;
import java.util.Map;

/**
 * Subclass of {@link ModelMap} that implements the {@link Model} interface.
 * Java 5 specific like the {@code Model} interface itself.
 *
 * <p>This is an implementation class exposed to handler methods by Spring MVC, typically via
 * a declaration of the {@link com.rocket.summer.framework.ui.Model} interface. There is no need to
 * build it within user code; a plain {@link com.rocket.summer.framework.ui.ModelMap} or even a just
 * a regular {@link Map} with String keys will be good enough to return a user model.
 *
 * @author Juergen Hoeller
 * @since 2.5.1
 */
@SuppressWarnings("serial")
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

    @Override
    public Map<String, Object> asMap() {
        return this;
    }

}

