package com.rocket.summer.framework.core.env;

import java.util.Map;

/**
 * {@link PropertySource} that reads keys and values from a {@code Map} object.
 *
 * @author Chris Beams
 * @since 3.1
 * @see PropertiesPropertySource
 */
public class MapPropertySource extends EnumerablePropertySource<Map<String, Object>> {

    public MapPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override
    public Object getProperty(String name) {
        return this.source.get(name);
    }

    @Override
    public String[] getPropertyNames() {
        return this.source.keySet().toArray(EMPTY_NAMES_ARRAY);
    }

}
