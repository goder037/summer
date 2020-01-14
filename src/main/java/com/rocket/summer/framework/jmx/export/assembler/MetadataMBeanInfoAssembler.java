package com.rocket.summer.framework.jmx.export.assembler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanNotificationInfo;

import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.jmx.export.metadata.InvalidMetadataException;
import com.rocket.summer.framework.jmx.export.metadata.JmxAttributeSource;
import com.rocket.summer.framework.jmx.export.metadata.JmxMetadataUtils;
import com.rocket.summer.framework.jmx.export.metadata.ManagedAttribute;
import com.rocket.summer.framework.jmx.export.metadata.ManagedMetric;
import com.rocket.summer.framework.jmx.export.metadata.ManagedNotification;
import com.rocket.summer.framework.jmx.export.metadata.ManagedOperation;
import com.rocket.summer.framework.jmx.export.metadata.ManagedOperationParameter;
import com.rocket.summer.framework.jmx.export.metadata.ManagedResource;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Implementation of the {@link MBeanInfoAssembler} interface that reads
 * the management interface information from source level metadata.
 *
 * <p>Uses the {@link JmxAttributeSource} strategy interface, so that
 * metadata can be read using any supported implementation. Out of the box,
 * Spring provides an implementation based on annotations:
 * {@code AnnotationJmxAttributeSource}.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Jennifer Hickey
 * @since 1.2
 * @see #setAttributeSource
 * @see com.rocket.summer.framework.jmx.export.annotation.AnnotationJmxAttributeSource
 */
public class MetadataMBeanInfoAssembler extends AbstractReflectiveMBeanInfoAssembler
        implements AutodetectCapableMBeanInfoAssembler, InitializingBean {

    private JmxAttributeSource attributeSource;


    /**
     * Create a new {@code MetadataMBeanInfoAssembler} which needs to be
     * configured through the {@link #setAttributeSource} method.
     */
    public MetadataMBeanInfoAssembler() {
    }

    /**
     * Create a new {@code MetadataMBeanInfoAssembler} for the given
     * {@code JmxAttributeSource}.
     * @param attributeSource the JmxAttributeSource to use
     */
    public MetadataMBeanInfoAssembler(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }


    /**
     * Set the {@code JmxAttributeSource} implementation to use for
     * reading the metadata from the bean class.
     * @see com.rocket.summer.framework.jmx.export.annotation.AnnotationJmxAttributeSource
     */
    public void setAttributeSource(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.attributeSource == null) {
            throw new IllegalArgumentException("Property 'attributeSource' is required");
        }
    }


    /**
     * Throws an IllegalArgumentException if it encounters a JDK dynamic proxy.
     * Metadata can only be read from target classes and CGLIB proxies!
     */
    @Override
    protected void checkManagedBean(Object managedBean) throws IllegalArgumentException {
        if (AopUtils.isJdkDynamicProxy(managedBean)) {
            throw new IllegalArgumentException(
                    "MetadataMBeanInfoAssembler does not support JDK dynamic proxies - " +
                            "export the target beans directly or use CGLIB proxies instead");
        }
    }

    /**
     * Used for autodetection of beans. Checks to see if the bean's class has a
     * {@code ManagedResource} attribute. If so it will add it list of included beans.
     * @param beanClass the class of the bean
     * @param beanName the name of the bean in the bean factory
     */
    @Override
    public boolean includeBean(Class<?> beanClass, String beanName) {
        return (this.attributeSource.getManagedResource(getClassToExpose(beanClass)) != null);
    }

    /**
     * Vote on the inclusion of an attribute accessor.
     * @param method the accessor method
     * @param beanKey the key associated with the MBean in the beans map
     * @return whether the method has the appropriate metadata
     */
    @Override
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return hasManagedAttribute(method) || hasManagedMetric(method);
    }

    /**
     * Votes on the inclusion of an attribute mutator.
     * @param method the mutator method
     * @param beanKey the key associated with the MBean in the beans map
     * @return whether the method has the appropriate metadata
     */
    @Override
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return hasManagedAttribute(method);
    }

    /**
     * Votes on the inclusion of an operation.
     * @param method the operation method
     * @param beanKey the key associated with the MBean in the beans map
     * @return whether the method has the appropriate metadata
     */
    @Override
    protected boolean includeOperation(Method method, String beanKey) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
        if (pd != null) {
            if (hasManagedAttribute(method)) {
                return true;
            }
        }
        return hasManagedOperation(method);
    }

    /**
     * Checks to see if the given Method has the {@code ManagedAttribute} attribute.
     */
    private boolean hasManagedAttribute(Method method) {
        return (this.attributeSource.getManagedAttribute(method) != null);
    }

    /**
     * Checks to see if the given Method has the {@code ManagedMetric} attribute.
     */
    private boolean hasManagedMetric(Method method) {
        return (this.attributeSource.getManagedMetric(method) != null);
    }

    /**
     * Checks to see if the given Method has the {@code ManagedOperation} attribute.
     * @param method the method to check
     */
    private boolean hasManagedOperation(Method method) {
        return (this.attributeSource.getManagedOperation(method) != null);
    }


    /**
     * Reads managed resource description from the source level metadata.
     * Returns an empty {@code String} if no description can be found.
     */
    @Override
    protected String getDescription(Object managedBean, String beanKey) {
        ManagedResource mr = this.attributeSource.getManagedResource(getClassToExpose(managedBean));
        return (mr != null ? mr.getDescription() : "");
    }

    /**
     * Creates a description for the attribute corresponding to this property
     * descriptor. Attempts to create the description using metadata from either
     * the getter or setter attributes, otherwise uses the property name.
     */
    @Override
    protected String getAttributeDescription(PropertyDescriptor propertyDescriptor, String beanKey) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();

        ManagedAttribute getter =
                (readMethod != null ? this.attributeSource.getManagedAttribute(readMethod) : null);
        ManagedAttribute setter =
                (writeMethod != null ? this.attributeSource.getManagedAttribute(writeMethod) : null);

        if (getter != null && StringUtils.hasText(getter.getDescription())) {
            return getter.getDescription();
        }
        else if (setter != null && StringUtils.hasText(setter.getDescription())) {
            return setter.getDescription();
        }

        ManagedMetric metric = (readMethod != null ? this.attributeSource.getManagedMetric(readMethod) : null);
        if (metric != null && StringUtils.hasText(metric.getDescription())) {
            return metric.getDescription();
        }

        return propertyDescriptor.getDisplayName();
    }

    /**
     * Retrieves the description for the supplied {@code Method} from the
     * metadata. Uses the method name is no description is present in the metadata.
     */
    @Override
    protected String getOperationDescription(Method method, String beanKey) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
        if (pd != null) {
            ManagedAttribute ma = this.attributeSource.getManagedAttribute(method);
            if (ma != null && StringUtils.hasText(ma.getDescription())) {
                return ma.getDescription();
            }
            ManagedMetric metric = this.attributeSource.getManagedMetric(method);
            if (metric != null && StringUtils.hasText(metric.getDescription())) {
                return metric.getDescription();
            }
            return method.getName();
        }
        else {
            ManagedOperation mo = this.attributeSource.getManagedOperation(method);
            if (mo != null && StringUtils.hasText(mo.getDescription())) {
                return mo.getDescription();
            }
            return method.getName();
        }
    }

    /**
     * Reads {@code MBeanParameterInfo} from the {@code ManagedOperationParameter}
     * attributes attached to a method. Returns an empty array of {@code MBeanParameterInfo}
     * if no attributes are found.
     */
    @Override
    protected MBeanParameterInfo[] getOperationParameters(Method method, String beanKey) {
        ManagedOperationParameter[] params = this.attributeSource.getManagedOperationParameters(method);
        if (ObjectUtils.isEmpty(params)) {
            return super.getOperationParameters(method, beanKey);
        }

        MBeanParameterInfo[] parameterInfo = new MBeanParameterInfo[params.length];
        Class<?>[] methodParameters = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            ManagedOperationParameter param = params[i];
            parameterInfo[i] =
                    new MBeanParameterInfo(param.getName(), methodParameters[i].getName(), param.getDescription());
        }
        return parameterInfo;
    }

    /**
     * Reads the {@link ManagedNotification} metadata from the {@code Class} of the managed resource
     * and generates and returns the corresponding {@link ModelMBeanNotificationInfo} metadata.
     */
    @Override
    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) {
        ManagedNotification[] notificationAttributes =
                this.attributeSource.getManagedNotifications(getClassToExpose(managedBean));
        ModelMBeanNotificationInfo[] notificationInfos =
                new ModelMBeanNotificationInfo[notificationAttributes.length];

        for (int i = 0; i < notificationAttributes.length; i++) {
            ManagedNotification attribute = notificationAttributes[i];
            notificationInfos[i] = JmxMetadataUtils.convertToModelMBeanNotificationInfo(attribute);
        }

        return notificationInfos;
    }

    /**
     * Adds descriptor fields from the {@code ManagedResource} attribute
     * to the MBean descriptor. Specifically, adds the {@code currencyTimeLimit},
     * {@code persistPolicy}, {@code persistPeriod}, {@code persistLocation}
     * and {@code persistName} descriptor fields if they are present in the metadata.
     */
    @Override
    protected void populateMBeanDescriptor(Descriptor desc, Object managedBean, String beanKey) {
        ManagedResource mr = this.attributeSource.getManagedResource(getClassToExpose(managedBean));
        if (mr == null) {
            throw new InvalidMetadataException(
                    "No ManagedResource attribute found for class: " + getClassToExpose(managedBean));
        }

        applyCurrencyTimeLimit(desc, mr.getCurrencyTimeLimit());

        if (mr.isLog()) {
            desc.setField(FIELD_LOG, "true");
        }
        if (StringUtils.hasLength(mr.getLogFile())) {
            desc.setField(FIELD_LOG_FILE, mr.getLogFile());
        }

        if (StringUtils.hasLength(mr.getPersistPolicy())) {
            desc.setField(FIELD_PERSIST_POLICY, mr.getPersistPolicy());
        }
        if (mr.getPersistPeriod() >= 0) {
            desc.setField(FIELD_PERSIST_PERIOD, Integer.toString(mr.getPersistPeriod()));
        }
        if (StringUtils.hasLength(mr.getPersistName())) {
            desc.setField(FIELD_PERSIST_NAME, mr.getPersistName());
        }
        if (StringUtils.hasLength(mr.getPersistLocation())) {
            desc.setField(FIELD_PERSIST_LOCATION, mr.getPersistLocation());
        }
    }

    /**
     * Adds descriptor fields from the {@code ManagedAttribute} attribute or the {@code ManagedMetric} attribute
     * to the attribute descriptor.
     */
    @Override
    protected void populateAttributeDescriptor(Descriptor desc, Method getter, Method setter, String beanKey) {
        if (getter != null && hasManagedMetric(getter)) {
            populateMetricDescriptor(desc, this.attributeSource.getManagedMetric(getter));
        }
        else {
            ManagedAttribute gma =
                    (getter == null) ? ManagedAttribute.EMPTY : this.attributeSource.getManagedAttribute(getter);
            ManagedAttribute sma =
                    (setter == null) ? ManagedAttribute.EMPTY : this.attributeSource.getManagedAttribute(setter);
            populateAttributeDescriptor(desc,gma,sma);
        }
    }

    private void populateAttributeDescriptor(Descriptor desc, ManagedAttribute gma, ManagedAttribute sma) {
        applyCurrencyTimeLimit(desc, resolveIntDescriptor(gma.getCurrencyTimeLimit(), sma.getCurrencyTimeLimit()));

        Object defaultValue = resolveObjectDescriptor(gma.getDefaultValue(), sma.getDefaultValue());
        desc.setField(FIELD_DEFAULT, defaultValue);

        String persistPolicy = resolveStringDescriptor(gma.getPersistPolicy(), sma.getPersistPolicy());
        if (StringUtils.hasLength(persistPolicy)) {
            desc.setField(FIELD_PERSIST_POLICY, persistPolicy);
        }
        int persistPeriod = resolveIntDescriptor(gma.getPersistPeriod(), sma.getPersistPeriod());
        if (persistPeriod >= 0) {
            desc.setField(FIELD_PERSIST_PERIOD, Integer.toString(persistPeriod));
        }
    }

    private void populateMetricDescriptor(Descriptor desc, ManagedMetric metric) {
        applyCurrencyTimeLimit(desc, metric.getCurrencyTimeLimit());

        if (StringUtils.hasLength(metric.getPersistPolicy())) {
            desc.setField(FIELD_PERSIST_POLICY, metric.getPersistPolicy());
        }
        if (metric.getPersistPeriod() >= 0) {
            desc.setField(FIELD_PERSIST_PERIOD, Integer.toString(metric.getPersistPeriod()));
        }

        if (StringUtils.hasLength(metric.getDisplayName())) {
            desc.setField(FIELD_DISPLAY_NAME, metric.getDisplayName());
        }

        if (StringUtils.hasLength(metric.getUnit())) {
            desc.setField(FIELD_UNITS, metric.getUnit());
        }

        if (StringUtils.hasLength(metric.getCategory())) {
            desc.setField(FIELD_METRIC_CATEGORY, metric.getCategory());
        }

        desc.setField(FIELD_METRIC_TYPE, metric.getMetricType().toString());
    }

    /**
     * Adds descriptor fields from the {@code ManagedAttribute} attribute
     * to the attribute descriptor. Specifically, adds the {@code currencyTimeLimit}
     * descriptor field if it is present in the metadata.
     */
    @Override
    protected void populateOperationDescriptor(Descriptor desc, Method method, String beanKey) {
        ManagedOperation mo = this.attributeSource.getManagedOperation(method);
        if (mo != null) {
            applyCurrencyTimeLimit(desc, mo.getCurrencyTimeLimit());
        }
    }

    /**
     * Determines which of two {@code int} values should be used as the value
     * for an attribute descriptor. In general, only the getter or the setter will
     * be have a non-negative value so we use that value. In the event that both values
     * are non-negative, we use the greater of the two. This method can be used to
     * resolve any {@code int} valued descriptor where there are two possible values.
     * @param getter the int value associated with the getter for this attribute
     * @param setter the int associated with the setter for this attribute
     */
    private int resolveIntDescriptor(int getter, int setter) {
        return (getter >= setter ? getter : setter);
    }

    /**
     * Locates the value of a descriptor based on values attached
     * to both the getter and setter methods. If both have values
     * supplied then the value attached to the getter is preferred.
     * @param getter the Object value associated with the get method
     * @param setter the Object value associated with the set method
     * @return the appropriate Object to use as the value for the descriptor
     */
    private Object resolveObjectDescriptor(Object getter, Object setter) {
        return (getter != null ? getter : setter);
    }

    /**
     * Locates the value of a descriptor based on values attached
     * to both the getter and setter methods. If both have values
     * supplied then the value attached to the getter is preferred.
     * The supplied default value is used to check to see if the value
     * associated with the getter has changed from the default.
     * @param getter the String value associated with the get method
     * @param setter the String value associated with the set method
     * @return the appropriate String to use as the value for the descriptor
     */
    private String resolveStringDescriptor(String getter, String setter) {
        return (StringUtils.hasLength(getter) ? getter : setter);
    }

}
