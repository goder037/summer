package com.rocket.summer.framework.core.type.filter;

import com.rocket.summer.framework.core.type.ClassMetadata;
import com.rocket.summer.framework.util.Assert;

import java.util.regex.Pattern;

/**
 * A simple filter for matching a fully-qualified class name with a regex {@link Pattern}.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 */
public class RegexPatternTypeFilter extends AbstractClassTestingTypeFilter {

    private final Pattern pattern;


    public RegexPatternTypeFilter(Pattern pattern) {
        Assert.notNull(pattern, "Pattern must not be null");
        this.pattern = pattern;
    }


    @Override
    protected boolean match(ClassMetadata metadata) {
        return this.pattern.matcher(metadata.getClassName()).matches();
    }

}

