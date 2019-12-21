package com.rocket.summer.framework.jmx.export.metadata;

/**
 * Metadata that indicates to expose a given bean property as JMX attribute.
 * Only valid when used on a JavaBean getter or setter.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see com.rocket.summer.framework.jmx.export.assembler.MetadataMBeanInfoAssembler
 * @see com.rocket.summer.framework.jmx.export.MBeanExporter
 */
public class ManagedAttribute extends AbstractJmxAttribute {

    public static final ManagedAttribute EMPTY = new ManagedAttribute();


    private Object defaultValue;

    private String persistPolicy;

    private int persistPeriod = -1;


    /**
     * Set the default value of this attribute.
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Return the default value of this attribute.
     */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setPersistPolicy(String persistPolicy) {
        this.persistPolicy = persistPolicy;
    }

    public String getPersistPolicy() {
        return this.persistPolicy;
    }

    public void setPersistPeriod(int persistPeriod) {
        this.persistPeriod = persistPeriod;
    }

    public int getPersistPeriod() {
        return this.persistPeriod;
    }

}
