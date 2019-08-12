package com.rocket.summer.framework.boot.autoconfigure.web;

import java.util.Set;

import com.rocket.summer.framework.util.PropertyPlaceholderHelper;

/**
 * {@link PropertyPlaceholderHelper} that doesn't allow recursive resolution.
 *
 * @author Phillip Webb
 */
class NonRecursivePropertyPlaceholderHelper extends PropertyPlaceholderHelper {

    NonRecursivePropertyPlaceholderHelper(String placeholderPrefix,
                                          String placeholderSuffix) {
        super(placeholderPrefix, placeholderSuffix);
    }

    @Override
    protected String parseStringValue(String strVal,
                                      PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {
        return super.parseStringValue(strVal,
                new NonRecursivePlaceholderResolver(placeholderResolver),
                visitedPlaceholders);
    }

    private static class NonRecursivePlaceholderResolver implements PlaceholderResolver {

        private final PlaceholderResolver resolver;

        NonRecursivePlaceholderResolver(PlaceholderResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            if (this.resolver instanceof NonRecursivePlaceholderResolver) {
                return null;
            }
            return this.resolver.resolvePlaceholder(placeholderName);
        }

    }

}

