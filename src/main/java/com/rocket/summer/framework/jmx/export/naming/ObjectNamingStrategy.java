package com.rocket.summer.framework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Strategy interface that encapsulates the creation of {@code ObjectName} instances.
 *
 * <p>Used by the {@code MBeanExporter} to obtain {@code ObjectName}s
 * when registering beans.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see com.rocket.summer.framework.jmx.export.MBeanExporter
 * @see javax.management.ObjectName
 */
public interface ObjectNamingStrategy {

    /**
     * Obtain an {@code ObjectName} for the supplied bean.
     * @param managedBean the bean that will be exposed under the
     * returned {@code ObjectName}
     * @param beanKey the key associated with this bean in the beans map
     * passed to the {@code MBeanExporter}
     * @return the {@code ObjectName} instance
     * @throws MalformedObjectNameException if the resulting {@code ObjectName} is invalid
     */
    ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException;

}

