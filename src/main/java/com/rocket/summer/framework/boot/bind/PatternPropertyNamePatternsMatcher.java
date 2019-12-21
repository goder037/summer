package com.rocket.summer.framework.boot.bind;

import com.rocket.summer.framework.util.PatternMatchUtils;

import java.util.Collection;

/**
 * {@link PropertyNamePatternsMatcher} that delegates to
 * {@link PatternMatchUtils#simpleMatch(String[], String)}.
 *
 * @author Phillip Webb
 * @since 1.2.0
 */
class PatternPropertyNamePatternsMatcher implements PropertyNamePatternsMatcher {

    private final String[] patterns;

    PatternPropertyNamePatternsMatcher(Collection<String> patterns) {
        this.patterns = (patterns != null) ? patterns.toArray(new String[patterns.size()])
                : new String[] {};
    }

    @Override
    public boolean matches(String propertyName) {
        return PatternMatchUtils.simpleMatch(this.patterns, propertyName);
    }

}

