package com.rocket.summer.framework.jmx.export.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.modelmbean.ModelMBeanNotificationInfo;

import com.rocket.summer.framework.jmx.export.metadata.JmxMetadataUtils;
import com.rocket.summer.framework.jmx.export.metadata.ManagedNotification;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Base class for MBeanInfoAssemblers that support configurable
 * JMX notification behavior.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AbstractConfigurableMBeanInfoAssembler extends AbstractReflectiveMBeanInfoAssembler {

    private ModelMBeanNotificationInfo[] notificationInfos;

    private final Map<String, ModelMBeanNotificationInfo[]> notificationInfoMappings =
            new HashMap<String, ModelMBeanNotificationInfo[]>();


    public void setNotificationInfos(ManagedNotification[] notificationInfos) {
        ModelMBeanNotificationInfo[] infos = new ModelMBeanNotificationInfo[notificationInfos.length];
        for (int i = 0; i < notificationInfos.length; i++) {
            ManagedNotification notificationInfo = notificationInfos[i];
            infos[i] = JmxMetadataUtils.convertToModelMBeanNotificationInfo(notificationInfo);
        }
        this.notificationInfos = infos;
    }

    public void setNotificationInfoMappings(Map<String, Object> notificationInfoMappings) {
        for (Map.Entry<String, Object> entry : notificationInfoMappings.entrySet()) {
            this.notificationInfoMappings.put(entry.getKey(), extractNotificationMetadata(entry.getValue()));
        }
    }


    @Override
    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) {
        ModelMBeanNotificationInfo[] result = null;
        if (StringUtils.hasText(beanKey)) {
            result = this.notificationInfoMappings.get(beanKey);
        }
        if (result == null) {
            result = this.notificationInfos;
        }
        return (result != null ? result : new ModelMBeanNotificationInfo[0]);
    }

    private ModelMBeanNotificationInfo[] extractNotificationMetadata(Object mapValue) {
        if (mapValue instanceof ManagedNotification) {
            ManagedNotification mn = (ManagedNotification) mapValue;
            return new ModelMBeanNotificationInfo[] {JmxMetadataUtils.convertToModelMBeanNotificationInfo(mn)};
        }
        else if (mapValue instanceof Collection) {
            Collection<?> col = (Collection<?>) mapValue;
            List<ModelMBeanNotificationInfo> result = new ArrayList<ModelMBeanNotificationInfo>();
            for (Object colValue : col) {
                if (!(colValue instanceof ManagedNotification)) {
                    throw new IllegalArgumentException(
                            "Property 'notificationInfoMappings' only accepts ManagedNotifications for Map values");
                }
                ManagedNotification mn = (ManagedNotification) colValue;
                result.add(JmxMetadataUtils.convertToModelMBeanNotificationInfo(mn));
            }
            return result.toArray(new ModelMBeanNotificationInfo[result.size()]);
        }
        else {
            throw new IllegalArgumentException(
                    "Property 'notificationInfoMappings' only accepts ManagedNotifications for Map values");
        }
    }

}

