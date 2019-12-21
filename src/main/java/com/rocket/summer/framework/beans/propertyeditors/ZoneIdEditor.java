package com.rocket.summer.framework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.time.ZoneId;

/**
 * Editor for {@code java.time.ZoneId}, translating zone ID Strings into {@code ZoneId}
 * objects. Exposes the {@code TimeZone} ID as a text representation.
 *
 * @author Nicholas Williams
 * @since 4.0
 * @see java.time.ZoneId
 * @see TimeZoneEditor
 */
public class ZoneIdEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(ZoneId.of(text));
    }

    @Override
    public String getAsText() {
        ZoneId value = (ZoneId) getValue();
        return (value != null ? value.getId() : "");
    }

}

