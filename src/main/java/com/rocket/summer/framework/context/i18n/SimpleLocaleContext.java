package com.rocket.summer.framework.context.i18n;

import com.rocket.summer.framework.util.Assert;

import java.util.Locale;

/**
 * Simple implementation of the {@link LocaleContext} interface,
 * always returning a specified <code>Locale</code>.
 *
 * @author Juergen Hoeller
 * @since 1.2
 */
public class SimpleLocaleContext implements LocaleContext {

    private final Locale locale;


    /**
     * Create a new SimpleLocaleContext that exposes the specified Locale.
     * Every <code>getLocale()</code> will return this Locale.
     * @param locale the Locale to expose
     */
    public SimpleLocaleContext(Locale locale) {
        Assert.notNull(locale, "Locale must not be null");
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public String toString() {
        return this.locale.toString();
    }

}

