package com.rocket.summer.framework.scheduling.annotation;

import com.rocket.summer.framework.context.annotation.AdviceMode;
import com.rocket.summer.framework.context.annotation.AdviceModeImportSelector;

/**
 * Selects which implementation of {@link AbstractAsyncConfiguration} should
 * be used based on the value of {@link EnableAsync#mode} on the importing
 * {@code @Configuration} class.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see EnableAsync
 * @see ProxyAsyncConfiguration
 */
public class AsyncConfigurationSelector extends AdviceModeImportSelector<EnableAsync> {

    private static final String ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME =
            "com.rocket.summer.framework.scheduling.aspectj.AspectJAsyncConfiguration";


    /**
     * Returns {@link ProxyAsyncConfiguration} or {@code AspectJAsyncConfiguration}
     * for {@code PROXY} and {@code ASPECTJ} values of {@link EnableAsync#mode()},
     * respectively.
     */
    @Override
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return new String[] {ProxyAsyncConfiguration.class.getName()};
            case ASPECTJ:
                return new String[] {ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME};
            default:
                return null;
        }
    }

}

