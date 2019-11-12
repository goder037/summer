package com.rocket.summer.framework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Interface that allows infrastructure components to provide their own
 * {@code ObjectName}s to the {@code MBeanExporter}.
 *
 * <p><b>Note:</b> This interface is mainly intended for internal usage.
 *
 * @author Rob Harrop
 * @since 1.2.2
 * @see com.rocket.summer.framework.jmx.export.MBeanExporter
 */
public interface SelfNaming {

    /**
     * Return the {@code ObjectName} for the implementing object.
     * @throws MalformedObjectNameException if thrown by the ObjectName constructor
     * @see javax.management.ObjectName#ObjectName(String)
     * @see javax.management.ObjectName#getInstance(String)
     * @see com.rocket.summer.framework.jmx.support.ObjectNameManager#getInstance(String)
     */
    ObjectName getObjectName() throws MalformedObjectNameException;

}

