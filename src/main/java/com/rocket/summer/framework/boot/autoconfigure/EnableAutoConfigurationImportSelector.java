package com.rocket.summer.framework.boot.autoconfigure;

import com.rocket.summer.framework.context.annotation.DeferredImportSelector;
import com.rocket.summer.framework.core.type.AnnotationMetadata;

/**
 * {@link DeferredImportSelector} to handle {@link EnableAutoConfiguration
 * auto-configuration}. This class can also be subclassed if a custom variant of
 * {@link EnableAutoConfiguration @EnableAutoConfiguration}. is needed.
 *
 * @deprecated as of 1.5 in favor of {@link AutoConfigurationImportSelector}
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Madhura Bhave
 * @since 1.3.0
 * @see EnableAutoConfiguration
 */
@Deprecated
public class EnableAutoConfigurationImportSelector
        extends AutoConfigurationImportSelector {

    @Override
    protected boolean isEnabled(AnnotationMetadata metadata) {
        if (getClass().equals(EnableAutoConfigurationImportSelector.class)) {
            return getEnvironment().getProperty(
                    EnableAutoConfiguration.ENABLED_OVERRIDE_PROPERTY, Boolean.class,
                    true);
        }
        return true;
    }

}
