package com.rocket.summer.framework.scheduling.annotation;

import com.rocket.summer.framework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Optional interface to be implemented by @{@link
 * com.rocket.summer.framework.context.annotation.Configuration Configuration} classes annotated
 * with @{@link EnableScheduling}. Typically used for setting a specific
 * {@link com.rocket.summer.framework.scheduling.TaskScheduler TaskScheduler} bean to be used when
 * executing scheduled tasks or for registering scheduled tasks in a <em>programmatic</em>
 * fashion as opposed to the <em>declarative</em> approach of using the @{@link Scheduled}
 * annotation. For example, this may be necessary when implementing {@link
 * com.rocket.summer.framework.scheduling.Trigger Trigger}-based tasks, which are not supported by
 * the {@code @Scheduled} annotation.
 *
 * <p>See @{@link EnableScheduling} for detailed usage examples.
 *
 * @author Chris Beams
 * @since 3.1
 * @see EnableScheduling
 * @see ScheduledTaskRegistrar
 */
public interface SchedulingConfigurer {

    /**
     * Callback allowing a {@link com.rocket.summer.framework.scheduling.TaskScheduler
     * TaskScheduler} and specific {@link com.rocket.summer.framework.scheduling.config.Task Task}
     * instances to be registered against the given the {@link ScheduledTaskRegistrar}
     * @param taskRegistrar the registrar to be configured.
     */
    void configureTasks(ScheduledTaskRegistrar taskRegistrar);

}

