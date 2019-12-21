package com.rocket.summer.framework.boot.env;

import com.rocket.summer.framework.core.env.EnumerablePropertySource;
import com.rocket.summer.framework.core.env.PropertySource;

import java.util.*;

/**
 * An mutable, enumerable, composite property source. New sources are added last (and
 * hence resolved with lowest priority).
 *
 * @author Dave Syer
 * @see PropertySource
 * @see EnumerablePropertySource
 */
public class EnumerableCompositePropertySource
        extends EnumerablePropertySource<Collection<PropertySource<?>>> {

    private volatile String[] names;

    public EnumerableCompositePropertySource(String sourceName) {
        super(sourceName, new LinkedHashSet<PropertySource<?>>());
    }

    @Override
    public Object getProperty(String name) {
        for (PropertySource<?> propertySource : getSource()) {
            Object value = propertySource.getProperty(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String[] getPropertyNames() {
        String[] result = this.names;
        if (result == null) {
            List<String> names = new ArrayList<String>();
            for (PropertySource<?> source : new ArrayList<PropertySource<?>>(
                    getSource())) {
                if (source instanceof EnumerablePropertySource) {
                    names.addAll(Arrays.asList(
                            ((EnumerablePropertySource<?>) source).getPropertyNames()));
                }
            }
            this.names = names.toArray(new String[0]);
            result = this.names;
        }
        return result;
    }

    public void add(PropertySource<?> source) {
        getSource().add(source);
        this.names = null;
    }

}

