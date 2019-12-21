package com.rocket.summer.framework.web.servlet;

import com.rocket.summer.framework.context.i18n.LocaleContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Extension of {@link LocaleResolver}, adding support for a rich locale context
 * (potentially including locale and time zone information).
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see com.rocket.summer.framework.context.i18n.LocaleContext
 * @see com.rocket.summer.framework.context.i18n.TimeZoneAwareLocaleContext
 * @see com.rocket.summer.framework.context.i18n.LocaleContextHolder
 * @see com.rocket.summer.framework.web.servlet.support.RequestContext#getTimeZone
 * @see com.rocket.summer.framework.web.servlet.support.RequestContextUtils#getTimeZone
 */
public interface LocaleContextResolver extends LocaleResolver {

    /**
     * Resolve the current locale context via the given request.
     * <p>This is primarily intended for framework-level processing; consider using
     * {@link com.rocket.summer.framework.web.servlet.support.RequestContextUtils} or
     * {@link com.rocket.summer.framework.web.servlet.support.RequestContext} for
     * application-level access to the current locale and/or time zone.
     * <p>The returned context may be a
     * {@link com.rocket.summer.framework.context.i18n.TimeZoneAwareLocaleContext},
     * containing a locale with associated time zone information.
     * Simply apply an {@code instanceof} check and downcast accordingly.
     * <p>Custom resolver implementations may also return extra settings in
     * the returned context, which again can be accessed through downcasting.
     * @param request the request to resolve the locale context for
     * @return the current locale context (never {@code null}
     * @see #resolveLocale(HttpServletRequest)
     * @see com.rocket.summer.framework.web.servlet.support.RequestContextUtils#getLocale
     * @see com.rocket.summer.framework.web.servlet.support.RequestContextUtils#getTimeZone
     */
    LocaleContext resolveLocaleContext(HttpServletRequest request);

    /**
     * Set the current locale context to the given one,
     * potentially including a locale with associated time zone information.
     * @param request the request to be used for locale modification
     * @param response the response to be used for locale modification
     * @param localeContext the new locale context, or {@code null} to clear the locale
     * @throws UnsupportedOperationException if the LocaleResolver implementation
     * does not support dynamic changing of the locale or time zone
     * @see #setLocale(HttpServletRequest, HttpServletResponse, Locale)
     * @see com.rocket.summer.framework.context.i18n.SimpleLocaleContext
     * @see com.rocket.summer.framework.context.i18n.SimpleTimeZoneAwareLocaleContext
     */
    void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext);

}

