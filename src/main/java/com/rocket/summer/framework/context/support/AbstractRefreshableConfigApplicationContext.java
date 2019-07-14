package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.beans.factory.BeanNameAware;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.util.SystemPropertyUtils;

public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
        implements BeanNameAware, InitializingBean {

    private String[] configLocations;

    private boolean setIdCalled = false;

    /**
     * Create a new AbstractRefreshableConfigApplicationContext with no parent.
     */
    public AbstractRefreshableConfigApplicationContext() {
    }


    /**
     * Create a new AbstractRefreshableConfigApplicationContext with the given parent context.
     * @param parent the parent context
     */
    public AbstractRefreshableConfigApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public void setId(String id) {
        super.setId(id);
        this.setIdCalled = true;
    }

    /**
     * Sets the id of this context to the bean name by default,
     * for cases where the context instance is itself defined as a bean.
     */
    public void setBeanName(String name) {
        if (!this.setIdCalled) {
            super.setId(name);
        }
    }

    /**
     * Set the config locations for this application context in init-param style,
     * i.e. with distinct locations separated by commas, semicolons or whitespace.
     * <p>If not set, the implementation may use a default as appropriate.
     */
    public void setConfigLocation(String location) {
        setConfigLocations(StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS));
    }

    /**
     * Triggers {@link #refresh()} if not refreshed in the concrete context's
     * constructor already.
     */
    public void afterPropertiesSet() {
        if (!isActive()) {
            refresh();
        }
    }

    /**
     * Resolve the given path, replacing placeholders with corresponding
     * system property values if necessary. Applied to config locations.
     * @param path the original file path
     * @return the resolved file path
     * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders
     */
    protected String resolvePath(String path) {
        return SystemPropertyUtils.resolvePlaceholders(path);
    }

    /**
     * Set the config locations for this application context.
     * <p>If not set, the implementation may use a default as appropriate.
     */
    public void setConfigLocations(String[] locations) {
        if (locations != null) {
            Assert.noNullElements(locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; i++) {
                this.configLocations[i] = resolvePath(locations[i]).trim();
            }
        }
        else {
            this.configLocations = null;
        }
    }

    /**
     * Return an array of resource locations, referring to the XML bean definition
     * files that this context should be built with. Can also include location
     * patterns, which will get resolved via a ResourcePatternResolver.
     * <p>The default implementation returns <code>null</code>. Subclasses can override
     * this to provide a set of resource locations to load bean definitions from.
     * @return an array of resource locations, or <code>null</code> if none
     * @see #getResources
     * @see #getResourcePatternResolver
     */
    protected String[] getConfigLocations() {
        return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
    }

    /**
     * Return the default config locations to use, for the case where no
     * explicit config locations have been specified.
     * <p>The default implementation returns <code>null</code>,
     * requiring explicit config locations.
     * @return an array of default config locations, if any
     * @see #setConfigLocations
     */
    protected String[] getDefaultConfigLocations() {
        return null;
    }
}
