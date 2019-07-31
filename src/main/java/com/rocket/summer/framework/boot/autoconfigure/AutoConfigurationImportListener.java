package com.rocket.summer.framework.boot.autoconfigure;

import java.util.EventListener;

/**
 * Listener that can be registered with {@code spring.factories} to receive details of
 * imported auto-configurations.
 * <p>
 * An {@link AutoConfigurationImportListener} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces, and their respective
 * methods will be called prior to
 * {@link #onAutoConfigurationImportEvent(AutoConfigurationImportEvent)}:
 * <ul>
 * <li>{@link EnvironmentAware}</li>
 * <li>{@link BeanFactoryAware}</li>
 * <li>{@link BeanClassLoaderAware}</li>
 * <li>{@link ResourceLoaderAware}</li>
 * </ul>
 *
 * @author Phillip Webb
 * @since 1.5.0
 */
public interface AutoConfigurationImportListener extends EventListener {

    /**
     * Handle an auto-configuration import event.
     * @param event the event to respond to
     */
    void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event);

}

