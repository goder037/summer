package com.rocket.summer.framework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.regex.Pattern;

/**
 * Editor for <code>java.util.regex.Pattern</code>, to directly populate a Pattern property.
 * Expects the same syntax as Pattern's <code>compile</code> method.
 *
 * @author Juergen Hoeller
 * @since 2.0.1
 * @see java.util.regex.Pattern
 * @see java.util.regex.Pattern#compile(String)
 */
public class PatternEditor extends PropertyEditorSupport {

    private final int flags;


    /**
     * Create a new PatternEditor with default settings.
     */
    public PatternEditor() {
        this.flags = 0;
    }

    /**
     * Create a new PatternEditor with the given settings.
     * @param flags the <code>java.util.regex.Pattern</code> flags to apply
     * @see java.util.regex.Pattern#compile(String, int)
     * @see java.util.regex.Pattern#CASE_INSENSITIVE
     * @see java.util.regex.Pattern#MULTILINE
     * @see java.util.regex.Pattern#DOTALL
     * @see java.util.regex.Pattern#UNICODE_CASE
     * @see java.util.regex.Pattern#CANON_EQ
     */
    public PatternEditor(int flags) {
        this.flags = flags;
    }


    public void setAsText(String text) {
        setValue(text != null ? Pattern.compile(text, this.flags) : null);
    }

    public String getAsText() {
        Pattern value = (Pattern) getValue();
        return (value != null ? value.pattern() : "");
    }

}

