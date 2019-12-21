package com.rocket.summer.framework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A Number formatter for percent values.
 *
 * <p>Delegates to {@link NumberFormat#getPercentInstance(Locale)}.
 * Configures BigDecimal parsing so there is no loss in precision.
 * The {@link #parse(String, Locale)} routine always returns a BigDecimal.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see #setLenient
 */
public class PercentFormatter extends AbstractNumberFormatter {

    protected NumberFormat getNumberFormat(Locale locale) {
        NumberFormat format = NumberFormat.getPercentInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return format;
    }

}
