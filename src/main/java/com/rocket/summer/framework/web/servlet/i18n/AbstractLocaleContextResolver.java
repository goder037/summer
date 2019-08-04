package com.rocket.summer.framework.web.servlet.i18n;

import com.rocket.summer.framework.context.i18n.SimpleLocaleContext;
import com.rocket.summer.framework.web.servlet.LocaleContextResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Abstract base class for {@link LocaleContextResolver} implementations.
 * Provides support for a default locale and a default time zone.
 *
 * <p>Also provides pre-implemented versions of {@link #resolveLocale} and {@link #setLocale},
 * delegating to {@link #resolveLocaleContext} and {@link #setLocaleContext}.
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see #setDefaultLocale
 * @see #setDefaultTimeZone
 */
public abstract class AbstractLocaleContextResolver extends AbstractLocaleResolver implements LocaleContextResolver {

    private TimeZone defaultTimeZone;


    /**
     * Set a default TimeZone that this resolver will return if no other time zone found.
     */
    public void setDefaultTimeZone(TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    /**
     * Return the default TimeZone that this resolver is supposed to fall back to, if any.
     */
    public TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }


    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return resolveLocaleContext(request).getLocale();
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        setLocaleContext(request, response, (locale != null ? new SimpleLocaleContext(locale) : null));
    }

}

