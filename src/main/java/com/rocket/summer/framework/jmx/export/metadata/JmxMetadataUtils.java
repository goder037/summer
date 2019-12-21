package com.rocket.summer.framework.jmx.export.metadata;

import javax.management.modelmbean.ModelMBeanNotificationInfo;

import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Utility methods for converting Spring JMX metadata into their plain JMX equivalents.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class JmxMetadataUtils {

    /**
     * Convert the supplied {@link ManagedNotification} into the corresponding
     * {@link javax.management.modelmbean.ModelMBeanNotificationInfo}.
     */
    public static ModelMBeanNotificationInfo convertToModelMBeanNotificationInfo(ManagedNotification notificationInfo) {
        String[] notifTypes = notificationInfo.getNotificationTypes();
        if (ObjectUtils.isEmpty(notifTypes)) {
            throw new IllegalArgumentException("Must specify at least one notification type");
        }

        String name = notificationInfo.getName();
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Must specify notification name");
        }

        String description = notificationInfo.getDescription();
        return new ModelMBeanNotificationInfo(notifTypes, name, description);
    }

}

