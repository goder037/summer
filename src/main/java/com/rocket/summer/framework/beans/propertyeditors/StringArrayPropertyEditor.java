package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * Custom {@link java.beans.PropertyEditor} for String arrays.
 *
 * <p>Strings must be in CSV format, with a customizable separator.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.rocket.summer.framework.util.StringUtils#delimitedListToStringArray
 * @see com.rocket.summer.framework.util.StringUtils#arrayToDelimitedString
 */
public class StringArrayPropertyEditor extends PropertyEditorSupport {

    /**
     * Default separator for splitting a String: a comma (",")
     */
    public static final String DEFAULT_SEPARATOR = ",";


    private final String separator;

    private final String charsToDelete;

    private final boolean emptyArrayAsNull;


    /**
     * Create a new StringArrayPropertyEditor with the default separator
     * (a comma).
     * <p>An empty text (without elements) will be turned into an empty array.
     */
    public StringArrayPropertyEditor() {
        this(DEFAULT_SEPARATOR, null, false);
    }

    /**
     * Create a new StringArrayPropertyEditor with the given separator.
     * <p>An empty text (without elements) will be turned into an empty array.
     * @param separator the separator to use for splitting a {@link String}
     */
    public StringArrayPropertyEditor(String separator) {
        this(separator, null, false);
    }

    /**
     * Create a new StringArrayPropertyEditor with the given separator.
     * @param separator the separator to use for splitting a {@link String}
     * @param emptyArrayAsNull <code>true</code> if an empty String array
     * is to be transformed into <code>null</code>
     */
    public StringArrayPropertyEditor(String separator, boolean emptyArrayAsNull) {
        this(separator, null, emptyArrayAsNull);
    }

    /**
     * Create a new StringArrayPropertyEditor with the given separator.
     * @param separator the separator to use for splitting a {@link String}
     * @param charsToDelete a set of characters to delete, in addition to
     * trimming an input String. Useful for deleting unwanted line breaks:
     * e.g. "\r\n\f" will delete all new lines and line feeds in a String.
     * @param emptyArrayAsNull <code>true</code> if an empty String array
     * is to be transformed into <code>null</code>
     */
    public StringArrayPropertyEditor(String separator, String charsToDelete, boolean emptyArrayAsNull) {
        this.separator = separator;
        this.charsToDelete = charsToDelete;
        this.emptyArrayAsNull = emptyArrayAsNull;
    }


    public void setAsText(String text) throws IllegalArgumentException {
        String[] array = StringUtils.delimitedListToStringArray(text, this.separator, this.charsToDelete);
        if (this.emptyArrayAsNull && array.length == 0) {
            setValue(null);
        }
        else {
            setValue(array);
        }
    }

    public String getAsText() {
        return StringUtils.arrayToDelimitedString(ObjectUtils.toObjectArray(getValue()), this.separator);
    }

}

