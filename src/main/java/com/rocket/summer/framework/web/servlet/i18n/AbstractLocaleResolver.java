package com.rocket.summer.framework.web.servlet.i18n;

import com.rocket.summer.framework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * Abstract base class for {@link LocaleResolver} implementations.
 * Provides support for a default locale.
 *
 * @author Juergen Hoeller
 * @since 1.2.9
 * @see #setDefaultLocale
 */
public abstract class AbstractLocaleResolver implements LocaleResolver {

    private Locale defaultLocale;


    /**
     * Set a default Locale that this resolver will return if no other locale found.
     */
    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * Return the default Locale that this resolver is supposed to fall back to, if any.
     */
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }

}
