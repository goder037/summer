package com.rocket.summer.framework.web.servlet.support;

import com.rocket.summer.framework.web.context.WebApplicationContext;
import com.rocket.summer.framework.web.context.support.WebApplicationContextUtils;
import com.rocket.summer.framework.web.servlet.*;
import com.rocket.summer.framework.web.ui.context.Theme;
import com.rocket.summer.framework.web.ui.context.ThemeSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for easy access to request-specific state which has been
 * set by the {@link org.springframework.web.servlet.DispatcherServlet}.
 *
 * <p>Supports lookup of current WebApplicationContext, LocaleResolver,
 * Locale, ThemeResolver, Theme, and MultipartResolver.
 *
 * @author Juergen Hoeller
 * @since 03.03.2003
 * @see RequestContext
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public abstract class RequestContextUtils {

    /**
     * Look for the WebApplicationContext associated with the DispatcherServlet
     * that has initiated request processing.
     * @param request current HTTP request
     * @return the request-specific web application context
     * @throws IllegalStateException if no servlet-specific context has been found
     */
    public static WebApplicationContext getWebApplicationContext(ServletRequest request)
            throws IllegalStateException {

        return getWebApplicationContext(request, null);
    }

    /**
     * Look for the WebApplicationContext associated with the DispatcherServlet
     * that has initiated request processing, and for the global context if none
     * was found associated with the current request. This method is useful to
     * allow components outside the framework, such as JSP tag handlers,
     * to access the most specific application context available.
     * @param request current HTTP request
     * @param servletContext current servlet context
     * @return the request-specific WebApplicationContext, or the global one
     * if no request-specific context has been found
     * @throws IllegalStateException if neither a servlet-specific nor a
     * global context has been found
     */
    public static WebApplicationContext getWebApplicationContext(
            ServletRequest request, ServletContext servletContext) throws IllegalStateException {

        WebApplicationContext webApplicationContext = (WebApplicationContext) request.getAttribute(
                DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (webApplicationContext == null) {
            if (servletContext == null) {
                throw new IllegalStateException("No WebApplicationContext found: not in a DispatcherServlet request?");
            }
            webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        }
        return webApplicationContext;
    }

    /**
     * Return the LocaleResolver that has been bound to the request by the
     * DispatcherServlet.
     * @param request current HTTP request
     * @return the current LocaleResolver, or <code>null</code> if not found
     */
    public static LocaleResolver getLocaleResolver(HttpServletRequest request) {
        return (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
    }

    /**
     * Retrieves the current locale from the given request,
     * using the LocaleResolver bound to the request by the DispatcherServlet
     * (if available), falling back to the request's accept-header Locale.
     * @param request current HTTP request
     * @return the current locale, either from the LocaleResolver or from
     * the plain request
     * @see #getLocaleResolver
     * @see javax.servlet.http.HttpServletRequest#getLocale()
     */
    public static Locale getLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = getLocaleResolver(request);
        if (localeResolver != null) {
            return localeResolver.resolveLocale(request);
        }
        else {
            return request.getLocale();
        }
    }

    /**
     * Return the ThemeResolver that has been bound to the request by the
     * DispatcherServlet.
     * @param request current HTTP request
     * @return the current ThemeResolver, or <code>null</code> if not found
     */
    public static ThemeResolver getThemeResolver(HttpServletRequest request) {
        return (ThemeResolver) request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE);
    }

    /**
     * Return the ThemeSource that has been bound to the request by the
     * DispatcherServlet.
     * @param request current HTTP request
     * @return the current ThemeSource
     */
    public static ThemeSource getThemeSource(HttpServletRequest request) {
        return (ThemeSource) request.getAttribute(DispatcherServlet.THEME_SOURCE_ATTRIBUTE);
    }

    /**
     * Retrieves the current theme from the given request, using the ThemeResolver
     * and ThemeSource bound to the request by the DispatcherServlet.
     * @param request current HTTP request
     * @return the current theme, or <code>null</code> if not found
     * @see #getThemeResolver
     */
    public static Theme getTheme(HttpServletRequest request) {
        ThemeResolver themeResolver = getThemeResolver(request);
        ThemeSource themeSource = getThemeSource(request);
        if (themeResolver != null && themeSource != null) {
            String themeName = themeResolver.resolveThemeName(request);
            return themeSource.getTheme(themeName);
        }
        else {
            return null;
        }
    }

    /**
     * Return a read-only {@link Map} with "input" flash attributes saved on a
     * previous request.
     * @param request the current request
     * @return a read-only Map, or {@code null}
     * @see FlashMap
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ?> getInputFlashMap(HttpServletRequest request) {
        return (Map<String, ?>) request.getAttribute(DispatcherServlet.INPUT_FLASH_MAP_ATTRIBUTE);
    }

    /**
     * Return the "output" FlashMap with attributes to save for a subsequent request.
     * @param request current request
     * @return a {@link FlashMap} instance, never {@code null}
     * @see FlashMap
     */
    public static FlashMap getOutputFlashMap(HttpServletRequest request) {
        return (FlashMap) request.getAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE);
    }

    /**
     * Return the FlashMapManager instance to save flash attributes with
     * before a redirect.
     * @param request the current request
     */
    public static FlashMapManager getFlashMapManager(HttpServletRequest request) {
        return (FlashMapManager) request.getAttribute(DispatcherServlet.FLASH_MAP_MANAGER_ATTRIBUTE);
    }

}

