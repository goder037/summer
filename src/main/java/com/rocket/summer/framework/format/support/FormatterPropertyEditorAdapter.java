package com.rocket.summer.framework.format.support;

import com.rocket.summer.framework.context.i18n.LocaleContextHolder;
import com.rocket.summer.framework.format.Formatter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 * Adapter that bridges between {@link Formatter} and {@link PropertyEditor}.
 *
 * @author Juergen Hoeller
 * @since 4.2
 */
public class FormatterPropertyEditorAdapter extends PropertyEditorSupport {

    private final Formatter<Object> formatter;


    /**
     * Create a new {@code FormatterPropertyEditorAdapter} for the given {@link Formatter}.
     * @param formatter the {@link Formatter} to wrap
     */
    public FormatterPropertyEditorAdapter(Formatter<?> formatter) {
        Assert.notNull(formatter, "Formatter must not be null");
        this.formatter = (Formatter<Object>) formatter;
    }


    /**
     * Determine the {@link Formatter}-declared field type.
     * @return the field type declared in the wrapped {@link Formatter} implementation
     * (never {@code null})
     * @throws IllegalArgumentException if the {@link Formatter}-declared field type
     * cannot be inferred
     */
    public Class<?> getFieldType() {
        return FormattingConversionService.getFieldType(this.formatter);
    }


    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            try {
                setValue(this.formatter.parse(text, LocaleContextHolder.getLocale()));
            }
            catch (IllegalArgumentException ex) {
                throw ex;
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Parse attempt failed for value [" + text + "]", ex);
            }
        }
        else {
            setValue(null);
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return (value != null ? this.formatter.print(value, LocaleContextHolder.getLocale()) : "");
    }

}
