package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.boot.context.properties.ConfigurationProperties;
import com.rocket.summer.framework.boot.context.properties.NestedConfigurationProperty;
import com.rocket.summer.framework.context.ResourceLoaderAware;
import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Properties used to configure resource handling.
 *
 * @author Phillip Webb
 * @author Brian Clozel
 * @author Dave Syer
 * @author Venil Noronha
 * @since 1.1.0
 */
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties implements ResourceLoaderAware, InitializingBean {

    private static final String[] SERVLET_RESOURCE_LOCATIONS = { "/" };

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };

    private static final String[] RESOURCE_LOCATIONS;

    static {
        RESOURCE_LOCATIONS = new String[CLASSPATH_RESOURCE_LOCATIONS.length
                + SERVLET_RESOURCE_LOCATIONS.length];
        System.arraycopy(SERVLET_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS, 0,
                SERVLET_RESOURCE_LOCATIONS.length);
        System.arraycopy(CLASSPATH_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS,
                SERVLET_RESOURCE_LOCATIONS.length, CLASSPATH_RESOURCE_LOCATIONS.length);
    }

    /**
     * Locations of static resources. Defaults to classpath:[/META-INF/resources/,
     * /resources/, /static/, /public/] plus context:/ (the root of the servlet context).
     */
    private String[] staticLocations = RESOURCE_LOCATIONS;

    /**
     * Cache period for the resources served by the resource handler, in seconds.
     */
    private Integer cachePeriod;

    /**
     * Enable default resource handling.
     */
    private boolean addMappings = true;

    private final Chain chain = new Chain();

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() {
        this.staticLocations = appendSlashIfNecessary(this.staticLocations);
    }

    public String[] getStaticLocations() {
        return this.staticLocations;
    }

    public void setStaticLocations(String[] staticLocations) {
        this.staticLocations = appendSlashIfNecessary(staticLocations);
    }

    private String[] appendSlashIfNecessary(String[] staticLocations) {
        String[] normalized = new String[staticLocations.length];
        for (int i = 0; i < staticLocations.length; i++) {
            String location = staticLocations[i];
            if (location != null) {
                normalized[i] = (location.endsWith("/") ? location : location + "/");
            }
        }
        return normalized;
    }

    public Resource getWelcomePage() {
        for (String location : getStaticWelcomePageLocations()) {
            Resource resource = this.resourceLoader.getResource(location);
            try {
                if (resource.exists()) {
                    resource.getURL();
                    return resource;
                }
            }
            catch (Exception ex) {
                // Ignore
            }
        }
        return null;
    }

    private String[] getStaticWelcomePageLocations() {
        String[] result = new String[this.staticLocations.length];
        for (int i = 0; i < result.length; i++) {
            String location = this.staticLocations[i];
            if (!location.endsWith("/")) {
                location = location + "/";
            }
            result[i] = location + "index.html";
        }
        return result;
    }

    List<Resource> getFaviconLocations() {
        List<Resource> locations = new ArrayList<Resource>(
                this.staticLocations.length + 1);
        if (this.resourceLoader != null) {
            for (String location : this.staticLocations) {
                locations.add(this.resourceLoader.getResource(location));
            }
        }
        locations.add(new ClassPathResource("/"));
        return Collections.unmodifiableList(locations);
    }

    public Integer getCachePeriod() {
        return this.cachePeriod;
    }

    public void setCachePeriod(Integer cachePeriod) {
        this.cachePeriod = cachePeriod;
    }

    public boolean isAddMappings() {
        return this.addMappings;
    }

    public void setAddMappings(boolean addMappings) {
        this.addMappings = addMappings;
    }

    public Chain getChain() {
        return this.chain;
    }

    /**
     * Configuration for the Spring Resource Handling chain.
     */
    public static class Chain {

        /**
         * Enable the Spring Resource Handling chain. Disabled by default unless at least
         * one strategy has been enabled.
         */
        private Boolean enabled;

        /**
         * Enable caching in the Resource chain.
         */
        private boolean cache = true;

        /**
         * Enable HTML5 application cache manifest rewriting.
         */
        private boolean htmlApplicationCache = false;

        /**
         * Enable resolution of already gzipped resources. Checks for a resource name
         * variant with the "*.gz" extension.
         */
        private boolean gzipped = false;

        @NestedConfigurationProperty
        private final Strategy strategy = new Strategy();

        /**
         * Return whether the resource chain is enabled. Return {@code null} if no
         * specific settings are present.
         * @return whether the resource chain is enabled or {@code null} if no specified
         * settings are present.
         */
        public Boolean getEnabled() {
            return getEnabled(getStrategy().getFixed().isEnabled(),
                    getStrategy().getContent().isEnabled(), this.enabled);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isCache() {
            return this.cache;
        }

        public void setCache(boolean cache) {
            this.cache = cache;
        }

        public Strategy getStrategy() {
            return this.strategy;
        }

        public boolean isHtmlApplicationCache() {
            return this.htmlApplicationCache;
        }

        public void setHtmlApplicationCache(boolean htmlApplicationCache) {
            this.htmlApplicationCache = htmlApplicationCache;
        }

        public boolean isGzipped() {
            return this.gzipped;
        }

        public void setGzipped(boolean gzipped) {
            this.gzipped = gzipped;
        }

        static Boolean getEnabled(boolean fixedEnabled, boolean contentEnabled,
                                  Boolean chainEnabled) {
            return (fixedEnabled || contentEnabled) ? Boolean.TRUE : chainEnabled;
        }

    }

    /**
     * Strategies for extracting and embedding a resource version in its URL path.
     */
    public static class Strategy {

        @NestedConfigurationProperty
        private final Fixed fixed = new Fixed();

        @NestedConfigurationProperty
        private final Content content = new Content();

        public Fixed getFixed() {
            return this.fixed;
        }

        public Content getContent() {
            return this.content;
        }

    }

    /**
     * Version Strategy based on content hashing.
     */
    public static class Content {

        /**
         * Enable the content Version Strategy.
         */
        private boolean enabled;

        /**
         * Comma-separated list of patterns to apply to the Version Strategy.
         */
        private String[] paths = new String[] { "/**" };

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getPaths() {
            return this.paths;
        }

        public void setPaths(String[] paths) {
            this.paths = paths;
        }

    }

    /**
     * Version Strategy based on a fixed version string.
     */
    public static class Fixed {

        /**
         * Enable the fixed Version Strategy.
         */
        private boolean enabled;

        /**
         * Comma-separated list of patterns to apply to the Version Strategy.
         */
        private String[] paths = new String[] { "/**" };

        /**
         * Version string to use for the Version Strategy.
         */
        private String version;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getPaths() {
            return this.paths;
        }

        public void setPaths(String[] paths) {
            this.paths = paths;
        }

        public String getVersion() {
            return this.version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

    }

}

