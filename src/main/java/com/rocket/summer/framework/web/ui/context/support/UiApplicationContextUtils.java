package com.rocket.summer.framework.web.ui.context.support;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.web.ui.context.HierarchicalThemeSource;
import com.rocket.summer.framework.web.ui.context.ThemeSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for UI application context implementations.
 * Provides support for a special bean named "themeSource",
 * of type {@link org.springframework.ui.context.ThemeSource}.
 *
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public abstract class UiApplicationContextUtils {

    /**
     * Name of the ThemeSource bean in the factory.
     * If none is supplied, theme resolution is delegated to the parent.
     * @see org.springframework.ui.context.ThemeSource
     */
    public static final String THEME_SOURCE_BEAN_NAME = "themeSource";


    private static final Log logger = LogFactory.getLog(UiApplicationContextUtils.class);


    /**
     * Initialize the ThemeSource for the given application context,
     * autodetecting a bean with the name "themeSource". If no such
     * bean is found, a default (empty) ThemeSource will be used.
     * @param context current application context
     * @return the initialized theme source (will never be <code>null</code>)
     * @see #THEME_SOURCE_BEAN_NAME
     */
    public static ThemeSource initThemeSource(ApplicationContext context) {
        if (context.containsLocalBean(THEME_SOURCE_BEAN_NAME)) {
            ThemeSource themeSource = context.getBean(THEME_SOURCE_BEAN_NAME, ThemeSource.class);
            // Make ThemeSource aware of parent ThemeSource.
            if (context.getParent() instanceof ThemeSource && themeSource instanceof HierarchicalThemeSource) {
                HierarchicalThemeSource hts = (HierarchicalThemeSource) themeSource;
                if (hts.getParentThemeSource() == null) {
                    // Only set parent context as parent ThemeSource if no parent ThemeSource
                    // registered already.
                    hts.setParentThemeSource((ThemeSource) context.getParent());
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Using ThemeSource [" + themeSource + "]");
            }
            return themeSource;
        }
        else {
            // Use default ThemeSource to be able to accept getTheme calls, either
            // delegating to parent context's default or to local ResourceBundleThemeSource.
            HierarchicalThemeSource themeSource = null;
            if (context.getParent() instanceof ThemeSource) {
                themeSource = new DelegatingThemeSource();
                themeSource.setParentThemeSource((ThemeSource) context.getParent());
            }
            else {
                themeSource = new ResourceBundleThemeSource();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to locate ThemeSource with name '" + THEME_SOURCE_BEAN_NAME +
                        "': using default [" + themeSource + "]");
            }
            return themeSource;
        }
    }

}

