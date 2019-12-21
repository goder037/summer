package com.rocket.summer.framework.boot.env;

import com.rocket.summer.framework.core.env.MutablePropertySources;
import com.rocket.summer.framework.core.env.PropertySource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.support.SpringFactoriesLoader;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;

/**
 * Utility that can be used to {@link MutablePropertySources} using
 * {@link PropertySourceLoader}s.
 *
 * @author Phillip Webb
 */
public class PropertySourcesLoader {

    private static final Log logger = LogFactory.getLog(PropertySourcesLoader.class);

    private final MutablePropertySources propertySources;

    private final List<PropertySourceLoader> loaders;

    /**
     * Create a new {@link PropertySourceLoader} instance backed by a new
     * {@link MutablePropertySources}.
     */
    public PropertySourcesLoader() {
        this(new MutablePropertySources());
    }

    /**
     * Create a new {@link PropertySourceLoader} instance backed by the specified
     * {@link MutablePropertySources}.
     * @param propertySources the destination property sources
     */
    public PropertySourcesLoader(MutablePropertySources propertySources) {
        Assert.notNull(propertySources, "PropertySources must not be null");
        this.propertySources = propertySources;
        this.loaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class,
                getClass().getClassLoader());
    }

    /**
     * Load the specified resource (if possible) and add it as the first source.
     * @param resource the source resource (may be {@code null}).
     * @return the loaded property source or {@code null}
     * @throws IOException if the source cannot be loaded
     */
    public PropertySource<?> load(Resource resource) throws IOException {
        return load(resource, null);
    }

    /**
     * Load the profile-specific properties from the specified resource (if any) and add
     * it as the first source.
     * @param resource the source resource (may be {@code null}).
     * @param profile a specific profile to load or {@code null} to load the default.
     * @return the loaded property source or {@code null}
     * @throws IOException if the source cannot be loaded
     */
    public PropertySource<?> load(Resource resource, String profile) throws IOException {
        return load(resource, resource.getDescription(), profile);
    }

    /**
     * Load the profile-specific properties from the specified resource (if any), give the
     * name provided and add it as the first source.
     * @param resource the source resource (may be {@code null}).
     * @param name the root property name (may be {@code null}).
     * @param profile a specific profile to load or {@code null} to load the default.
     * @return the loaded property source or {@code null}
     * @throws IOException if the source cannot be loaded
     */
    public PropertySource<?> load(Resource resource, String name, String profile)
            throws IOException {
        return load(resource, null, name, profile);
    }

    /**
     * Load the profile-specific properties from the specified resource (if any), give the
     * name provided and add it to a group of property sources identified by the group
     * name. Property sources are added to the end of a group, but new groups are added as
     * the first in the chain being assembled. This means the normal sequence of calls is
     * to first create the group for the default (null) profile, and then add specific
     * groups afterwards (with the highest priority last). Property resolution from the
     * resulting sources will consider all keys for a given group first and then move to
     * the next group.
     * @param resource the source resource (may be {@code null}).
     * @param group an identifier for the group that this source belongs to
     * @param name the root property name (may be {@code null}).
     * @param profile a specific profile to load or {@code null} to load the default.
     * @return the loaded property source or {@code null}
     * @throws IOException if the source cannot be loaded
     */
    public PropertySource<?> load(Resource resource, String group, String name,
                                  String profile) throws IOException {
        if (isFile(resource)) {
            String sourceName = generatePropertySourceName(name, profile);
            for (PropertySourceLoader loader : this.loaders) {
                if (canLoadFileExtension(loader, resource)) {
                    PropertySource<?> specific = loader.load(sourceName, resource,
                            profile);
                    addPropertySource(group, specific);
                    return specific;
                }
            }
        }
        return null;
    }

    private boolean isFile(Resource resource) {
        return resource != null && resource.exists() && StringUtils
                .hasText(StringUtils.getFilenameExtension(resource.getFilename()));
    }

    private String generatePropertySourceName(String name, String profile) {
        return (profile != null) ? name + "#" + profile : name;
    }

    private boolean canLoadFileExtension(PropertySourceLoader loader, Resource resource) {
        String filename = resource.getFilename().toLowerCase(Locale.ENGLISH);
        for (String extension : loader.getFileExtensions()) {
            if (filename.endsWith("." + extension.toLowerCase(Locale.ENGLISH))) {
                return true;
            }
        }
        return false;
    }

    private void addPropertySource(String basename, PropertySource<?> source) {

        if (source == null) {
            return;
        }

        if (basename == null) {
            this.propertySources.addLast(source);
            return;
        }

        EnumerableCompositePropertySource group = getGeneric(basename);
        group.add(source);
        logger.trace("Adding PropertySource: " + source + " in group: " + basename);
        if (this.propertySources.contains(group.getName())) {
            this.propertySources.replace(group.getName(), group);
        }
        else {
            this.propertySources.addFirst(group);
        }

    }

    private EnumerableCompositePropertySource getGeneric(String name) {
        PropertySource<?> source = this.propertySources.get(name);
        if (source instanceof EnumerableCompositePropertySource) {
            return (EnumerableCompositePropertySource) source;
        }
        EnumerableCompositePropertySource composite = new EnumerableCompositePropertySource(
                name);
        return composite;
    }

    /**
     * Return the {@link MutablePropertySources} being loaded.
     * @return the property sources
     */
    public MutablePropertySources getPropertySources() {
        return this.propertySources;
    }

    /**
     * Returns all file extensions that could be loaded.
     * @return the file extensions
     */
    public Set<String> getAllFileExtensions() {
        Set<String> fileExtensions = new LinkedHashSet<String>();
        for (PropertySourceLoader loader : this.loaders) {
            fileExtensions.addAll(Arrays.asList(loader.getFileExtensions()));
        }
        return fileExtensions;
    }

}

