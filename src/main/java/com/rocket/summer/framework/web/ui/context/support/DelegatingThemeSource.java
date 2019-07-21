package com.rocket.summer.framework.web.ui.context.support;

import com.rocket.summer.framework.web.ui.context.HierarchicalThemeSource;
import com.rocket.summer.framework.web.ui.context.Theme;
import com.rocket.summer.framework.web.ui.context.ThemeSource;

/**
 * Empty ThemeSource that delegates all calls to the parent ThemeSource.
 * If no parent is available, it simply won't resolve any theme.
 *
 * <p>Used as placeholder by UiApplicationContextUtils, if a context doesn't
 * define its own ThemeSource. Not intended for direct use in applications.
 *
 * @author Juergen Hoeller
 * @since 1.2.4
 * @see UiApplicationContextUtils
 */
public class DelegatingThemeSource implements HierarchicalThemeSource {

    private ThemeSource parentThemeSource;


    public void setParentThemeSource(ThemeSource parentThemeSource) {
        this.parentThemeSource = parentThemeSource;
    }

    public ThemeSource getParentThemeSource() {
        return parentThemeSource;
    }


    public Theme getTheme(String themeName) {
        if (this.parentThemeSource != null) {
            return this.parentThemeSource.getTheme(themeName);
        } else {
            return null;
        }
    }
}

