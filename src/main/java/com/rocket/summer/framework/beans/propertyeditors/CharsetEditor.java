package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.nio.charset.Charset;

/**
 * Editor for {@link Charset}, to directly populate a Charset property.
 *
 * <p>Expects the same syntax as Charset's {@link java.nio.charset.Charset#name()},
 * e.g. <code>UTF-8</code>, <code>ISO-8859-16</code>, etc.
 *
 * @author Arjen Poutsma
 * @since 2.5.4
 * @see Charset
 */
public class CharsetEditor extends PropertyEditorSupport {

    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            setValue(Charset.forName(text));
        }
        else {
            setValue(null);
        }
    }

    public String getAsText() {
        Charset value = (Charset) getValue();
        return (value != null ? value.name() : "");
    }

}

