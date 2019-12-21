package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.util.UUID;

/**
 * Editor for {@code java.util.UUID}, translating UUID
 * String representations into UUID objects and back.
 *
 * @author Juergen Hoeller
 * @since 3.0.1
 * @see java.util.UUID
 */
public class UUIDEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            setValue(UUID.fromString(text));
        }
        else {
            setValue(null);
        }
    }

    @Override
    public String getAsText() {
        UUID value = (UUID) getValue();
        return (value != null ? value.toString() : "");
    }

}
