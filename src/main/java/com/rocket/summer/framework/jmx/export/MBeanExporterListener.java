package com.rocket.summer.framework.jmx.export;

import javax.management.ObjectName;

/**
 * A listener that allows application code to be notified when an MBean is
 * registered and unregistered via an {@link MBeanExporter}.
 *
 * @author Rob Harrop
 * @since 1.2.2
 * @see com.rocket.summer.framework.jmx.export.MBeanExporter#setListeners
 */
public interface MBeanExporterListener {

    /**
     * Called by {@link MBeanExporter} after an MBean has been <i>successfully</i>
     * registered with an {@link javax.management.MBeanServer}.
     * @param objectName the {@code ObjectName} of the registered MBean
     */
    void mbeanRegistered(ObjectName objectName);

    /**
     * Called by {@link MBeanExporter} after an MBean has been <i>successfully</i>
     * unregistered from an {@link javax.management.MBeanServer}.
     * @param objectName the {@code ObjectName} of the unregistered MBean
     */
    void mbeanUnregistered(ObjectName objectName);

}

