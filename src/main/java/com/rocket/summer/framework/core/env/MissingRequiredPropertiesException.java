package com.rocket.summer.framework.core.env;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Exception thrown when required properties are not found.
 *
 * @author Chris Beams
 * @since 3.1
 * @see ConfigurablePropertyResolver#setRequiredProperties(String...)
 * @see ConfigurablePropertyResolver#validateRequiredProperties()
 * @see org.springframework.context.support.AbstractApplicationContext#prepareRefresh()
 */
public class MissingRequiredPropertiesException extends IllegalStateException {

    private final Set<String> missingRequiredProperties = new LinkedHashSet<String>();

    /**
     * Return the set of properties marked as required but not present
     * upon validation.
     * @see ConfigurablePropertyResolver#setRequiredProperties(String...)
     * @see ConfigurablePropertyResolver#validateRequiredProperties()
     */
    public Set<String> getMissingRequiredProperties() {
        return missingRequiredProperties;
    }

    void addMissingRequiredProperty(String key) {
        missingRequiredProperties.add(key);
    }

    @Override
    public String getMessage() {
        return String.format(
                "The following properties were declared as required but could " +
                        "not be resolved: %s", this.getMissingRequiredProperties());
    }
}
