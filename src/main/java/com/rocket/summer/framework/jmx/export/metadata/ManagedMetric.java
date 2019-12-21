package com.rocket.summer.framework.jmx.export.metadata;

import com.rocket.summer.framework.jmx.support.MetricType;
import com.rocket.summer.framework.util.Assert;

/**
 * Metadata that indicates to expose a given bean property as a JMX attribute,
 * with additional descriptor properties that indicate that the attribute is a
 * metric. Only valid when used on a JavaBean getter.
 *
 * @author Jennifer Hickey
 * @since 3.0
 * @see com.rocket.summer.framework.jmx.export.assembler.MetadataMBeanInfoAssembler
 */
public class ManagedMetric extends AbstractJmxAttribute {

    private String category = "";

    private String displayName = "";

    private MetricType metricType = MetricType.GAUGE;

    private int persistPeriod = -1;

    private String persistPolicy = "";

    private String unit = "";


    /**
     * The category of this metric (ex. throughput, performance, utilization).
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * The category of this metric (ex. throughput, performance, utilization).
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * A display name for this metric.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * A display name for this metric.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * A description of how this metric's values change over time.
     */
    public void setMetricType(MetricType metricType) {
        Assert.notNull(metricType, "MetricType must not be null");
        this.metricType = metricType;
    }

    /**
     * A description of how this metric's values change over time.
     */
    public MetricType getMetricType() {
        return this.metricType;
    }

    /**
     * The persist period for this metric.
     */
    public void setPersistPeriod(int persistPeriod) {
        this.persistPeriod = persistPeriod;
    }

    /**
     * The persist period for this metric.
     */
    public int getPersistPeriod() {
        return this.persistPeriod;
    }

    /**
     * The persist policy for this metric.
     */
    public void setPersistPolicy(String persistPolicy) {
        this.persistPolicy = persistPolicy;
    }

    /**
     * The persist policy for this metric.
     */
    public String getPersistPolicy() {
        return this.persistPolicy;
    }

    /**
     * The expected unit of measurement values.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * The expected unit of measurement values.
     */
    public String getUnit() {
        return this.unit;
    }

}

