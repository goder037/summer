package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.core.env.SimpleCommandLinePropertySource;
import com.rocket.summer.framework.util.Assert;

import java.util.*;

/**
 * Default implementation of {@link ApplicationArguments}.
 *
 * @author Phillip Webb
 * @since 1.4.1
 */
public class DefaultApplicationArguments implements ApplicationArguments {

    private final Source source;

    private final String[] args;

    public DefaultApplicationArguments(String[] args) {
        Assert.notNull(args, "Args must not be null");
        this.source = new Source(args);
        this.args = args;
    }

    @Override
    public String[] getSourceArgs() {
        return this.args;
    }

    @Override
    public Set<String> getOptionNames() {
        String[] names = this.source.getPropertyNames();
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(names)));
    }

    @Override
    public boolean containsOption(String name) {
        return this.source.containsProperty(name);
    }

    @Override
    public List<String> getOptionValues(String name) {
        List<String> values = this.source.getOptionValues(name);
        return (values != null) ? Collections.unmodifiableList(values) : null;
    }

    @Override
    public List<String> getNonOptionArgs() {
        return this.source.getNonOptionArgs();
    }

    private static class Source extends SimpleCommandLinePropertySource {

        Source(String[] args) {
            super(args);
        }

        @Override
        public List<String> getNonOptionArgs() {
            return super.getNonOptionArgs();
        }

        @Override
        public List<String> getOptionValues(String name) {
            return super.getOptionValues(name);
        }

    }

}

