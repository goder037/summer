package com.rocket.summer.framework.boot.env;

import com.rocket.summer.framework.core.env.PropertiesPropertySource;
import com.rocket.summer.framework.core.env.PropertySource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Strategy to load '.properties' files into a {@link PropertySource}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
public class PropertiesPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[] { "properties", "xml" };
    }

    @Override
    public PropertySource<?> load(String name, Resource resource, String profile)
            throws IOException {
        if (profile == null) {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            if (!properties.isEmpty()) {
                return new PropertiesPropertySource(name, properties);
            }
        }
        return null;
    }

}

