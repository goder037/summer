package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.util.TimeZone;

/**
 * Editor for {@code java.util.TimeZone}, translating timezone IDs into
 * {@code TimeZone} objects. Exposes the {@code TimeZone} ID as a text
 * representation.
 *
 * @author Juergen Hoeller
 * @author Nicholas Williams
 * @since 3.0
 * @see java.util.TimeZone
 * @see ZoneIdEditor
 */
public class TimeZoneEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(StringUtils.parseTimeZoneString(text));
    }

    @Override
    public String getAsText() {
        TimeZone value = (TimeZone) getValue();
        return (value != null ? value.getID() : "");
    }

}
