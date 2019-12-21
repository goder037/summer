package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.util.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * Editor for <code>java.util.Locale</code>, to directly populate a Locale property.
 *
 * <p>Expects the same syntax as Locale's <code>toString</code>, i.e. language +
 * optionally country + optionally variant, separated by "_" (e.g. "en", "en_US").
 * Also accepts spaces as separators, as alternative to underscores.
 *
 * @author Juergen Hoeller
 * @since 26.05.2003
 * @see java.util.Locale
 * @see com.rocket.summer.framework.util.StringUtils#parseLocaleString
 */
public class LocaleEditor extends PropertyEditorSupport {

    public void setAsText(String text) {
        setValue(StringUtils.parseLocaleString(text));
    }

    public String getAsText() {
        Object value = getValue();
        return (value != null ? value.toString() : "");
    }

}

