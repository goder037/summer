package com.rocket.summer.framework.context;

/**
 * Interface defining methods for start/stop lifecycle control.
 * The typical use case for this is to control asynchronous processing.
 *
 * <p>Can be implemented by both components (typically a Spring bean defined in
 * a Spring {@link com.rocket.summer.framework.beans.factory.BeanFactory}) and containers
 * (typically a Spring {@link ApplicationContext}). Containers will propagate
 * start/stop signals to all components that apply.
 *
 * <p>Can be used for direct invocations or for management operations via JMX.
 * In the latter case, the {@link com.rocket.summer.framework.jmx.export.MBeanExporter}
 * will typically be defined with an
 * {@link com.rocket.summer.framework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler},
 * restricting the visibility of activity-controlled components to the Lifecycle
 * interface.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ConfigurableApplicationContext
 * @see com.rocket.summer.framework.jms.listener.AbstractMessageListenerContainer
 * @see com.rocket.summer.framework.scheduling.quartz.SchedulerFactoryBean
 */
public interface Lifecycle {

    /**
     * Start this component.
     * Should not throw an exception if the component is already running.
     * <p>In the case of a container, this will propagate the start signal
     * to all components that apply.
     */
    void start();

    /**
     * Stop this component.
     * Should not throw an exception if the component isn't started yet.
     * <p>In the case of a container, this will propagate the stop signal
     * to all components that apply.
     */
    void stop();

    /**
     * Check whether this component is currently running.
     * <p>In the case of a container, this will return <code>true</code>
     * only if <i>all</i> components that apply are currently running.
     * @return whether the component is currently running
     */
    boolean isRunning();

}
