package com.rocket.summer.framework.core.io.support;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.SystemPropertyUtils;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.*;

/**
 * Editor for {@link com.rocket.summer.framework.core.io.Resource} arrays, to
 * automatically convert <code>String</code> location patterns
 * (e.g. <code>"file:C:/my*.txt"</code> or <code>"classpath*:myfile.txt"</code>)
 * to <code>Resource</code> array properties. Can also translate a collection
 * or array of location patterns into a merged Resource array.
 *
 * <p>The path may contain <code>${...}</code> placeholders, to be resolved
 * as system properties: e.g. <code>${user.dir}</code>.
 *
 * <p>Delegates to a {@link ResourcePatternResolver},
 * by default using a {@link PathMatchingResourcePatternResolver}.
 *
 * @author Juergen Hoeller
 * @since 1.1.2
 * @see com.rocket.summer.framework.core.io.Resource
 * @see ResourcePatternResolver
 * @see PathMatchingResourcePatternResolver
 * @see com.rocket.summer.framework.util.SystemPropertyUtils#resolvePlaceholders
 * @see System#getProperty(String)
 */
public class ResourceArrayPropertyEditor extends PropertyEditorSupport {

    private final ResourcePatternResolver resourcePatternResolver;


    /**
     * Create a new ResourceArrayPropertyEditor with a default
     * PathMatchingResourcePatternResolver.
     * @see PathMatchingResourcePatternResolver
     */
    public ResourceArrayPropertyEditor() {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    /**
     * Create a new ResourceArrayPropertyEditor with the given ResourcePatternResolver.
     * @param resourcePatternResolver the ResourcePatternResolver to use
     */
    public ResourceArrayPropertyEditor(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }


    /**
     * Treat the given text as location pattern and convert it to a Resource array.
     */
    public void setAsText(String text) {
        String pattern = resolvePath(text).trim();
        try {
            setValue(this.resourcePatternResolver.getResources(pattern));
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(
                    "Could not resolve resource location pattern [" + pattern + "]: " + ex.getMessage());
        }
    }

    /**
     * Treat the given value as collection or array and convert it to a Resource array.
     * Considers String elements as location patterns, and takes Resource elements as-is.
     */
    public void setValue(Object value) throws IllegalArgumentException {
        if (value instanceof Collection || (value instanceof Object[] && !(value instanceof Resource[]))) {
            Collection input = (value instanceof Collection ? (Collection) value : Arrays.asList((Object[]) value));
            List merged = new ArrayList();
            for (Iterator it = input.iterator(); it.hasNext();) {
                Object element = it.next();
                if (element instanceof String) {
                    // A location pattern: resolve it into a Resource array.
                    // Might point to a single resource or to multiple resources.
                    String pattern = resolvePath((String) element).trim();
                    try {
                        Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                        for (int i = 0; i < resources.length; i++) {
                            Resource resource = resources[i];
                            if (!merged.contains(resource)) {
                                merged.add(resource);
                            }
                        }
                    }
                    catch (IOException ex) {
                        throw new IllegalArgumentException(
                                "Could not resolve resource location pattern [" + pattern + "]: " + ex.getMessage());
                    }
                }
                else if (element instanceof Resource) {
                    // A Resource object: add it to the result.
                    if (!merged.contains(element)) {
                        merged.add(element);
                    }
                }
                else {
                    throw new IllegalArgumentException("Cannot convert element [" + element + "] to [" +
                            Resource.class.getName() + "]: only location String and Resource object supported");
                }
            }
            super.setValue(merged.toArray(new Resource[merged.size()]));
        }

        else {
            // An arbitrary value: probably a String or a Resource array.
            // setAsText will be called for a String; a Resource array will be used as-is.
            super.setValue(value);
        }
    }

    /**
     * Resolve the given path, replacing placeholders with
     * corresponding system property values if necessary.
     * @param path the original file path
     * @return the resolved file path
     * @see com.rocket.summer.framework.util.SystemPropertyUtils#resolvePlaceholders
     */
    protected String resolvePath(String path) {
        return SystemPropertyUtils.resolvePlaceholders(path);
    }

}

