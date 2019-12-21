package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.util.NumberUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;

/**
 * Property editor for any Number subclass such as Short, Integer, Long,
 * BigInteger, Float, Double, BigDecimal. Can use a given NumberFormat for
 * (locale-specific) parsing and rendering, or alternatively the default
 * <code>decode</code> / <code>valueOf</code> / <code>toString</code> methods.
 *
 * <p>This is not meant to be used as system PropertyEditor but rather
 * as locale-specific number editor within custom controller code,
 * parsing user-entered number strings into Number properties of beans
 * and rendering them in the UI form.
 *
 * <p>In web MVC code, this editor will typically be registered with
 * <code>binder.registerCustomEditor</code> calls in a custom
 * <code>initBinder</code> method.
 *
 * @author Juergen Hoeller
 * @since 06.06.2003
 * @see java.lang.Number
 * @see java.text.NumberFormat
 * @see com.rocket.summer.framework.validation.DataBinder#registerCustomEditor
 * @see com.rocket.summer.framework.web.servlet.mvc.BaseCommandController#initBinder
 */
public class CustomNumberEditor extends PropertyEditorSupport {

    private final Class numberClass;

    private final NumberFormat numberFormat;

    private final boolean allowEmpty;


    /**
     * Create a new CustomNumberEditor instance, using the default
     * <code>valueOf</code> methods for parsing and <code>toString</code>
     * methods for rendering.
     * <p>The "allowEmpty" parameter states if an empty String should
     * be allowed for parsing, i.e. get interpreted as <code>null</code> value.
     * Else, an IllegalArgumentException gets thrown in that case.
     * @param numberClass Number subclass to generate
     * @param allowEmpty if empty strings should be allowed
     * @throws IllegalArgumentException if an invalid numberClass has been specified
     * @see com.rocket.summer.framework.util.NumberUtils#parseNumber(String, Class)
     * @see Integer#valueOf
     * @see Integer#toString
     */
    public CustomNumberEditor(Class numberClass, boolean allowEmpty) throws IllegalArgumentException {
        this(numberClass, null, allowEmpty);
    }

    /**
     * Create a new CustomNumberEditor instance, using the given NumberFormat
     * for parsing and rendering.
     * <p>The allowEmpty parameter states if an empty String should
     * be allowed for parsing, i.e. get interpreted as <code>null</code> value.
     * Else, an IllegalArgumentException gets thrown in that case.
     * @param numberClass Number subclass to generate
     * @param numberFormat NumberFormat to use for parsing and rendering
     * @param allowEmpty if empty strings should be allowed
     * @throws IllegalArgumentException if an invalid numberClass has been specified
     * @see com.rocket.summer.framework.util.NumberUtils#parseNumber(String, Class, java.text.NumberFormat)
     * @see java.text.NumberFormat#parse
     * @see java.text.NumberFormat#format
     */
    public CustomNumberEditor(Class numberClass, NumberFormat numberFormat, boolean allowEmpty)
            throws IllegalArgumentException {

        if (numberClass == null || !Number.class.isAssignableFrom(numberClass)) {
            throw new IllegalArgumentException("Property class must be a subclass of Number");
        }
        this.numberClass = numberClass;
        this.numberFormat = numberFormat;
        this.allowEmpty = allowEmpty;
    }


    /**
     * Parse the Number from the given text, using the specified NumberFormat.
     */
    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        }
        else if (this.numberFormat != null) {
            // Use given NumberFormat for parsing text.
            setValue(NumberUtils.parseNumber(text, this.numberClass, this.numberFormat));
        }
        else {
            // Use default valueOf methods for parsing text.
            setValue(NumberUtils.parseNumber(text, this.numberClass));
        }
    }

    /**
     * Coerce a Number value into the required target class, if necessary.
     */
    public void setValue(Object value) {
        if (value instanceof Number) {
            super.setValue(NumberUtils.convertNumberToTargetClass((Number) value, this.numberClass));
        }
        else {
            super.setValue(value);
        }
    }

    /**
     * Format the Number as String, using the specified NumberFormat.
     */
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return "";
        }
        if (this.numberFormat != null) {
            // Use NumberFormat for rendering value.
            return this.numberFormat.format(value);
        }
        else {
            // Use toString method for rendering value.
            return value.toString();
        }
    }

}

