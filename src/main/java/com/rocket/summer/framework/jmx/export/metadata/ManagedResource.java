package com.rocket.summer.framework.jmx.export.metadata;

/**
 * Metadata indicating that instances of an annotated class
 * are to be registered with a JMX server.
 * Only valid when used on a {@code Class}.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see com.rocket.summer.framework.jmx.export.assembler.MetadataMBeanInfoAssembler
 * @see com.rocket.summer.framework.jmx.export.naming.MetadataNamingStrategy
 * @see com.rocket.summer.framework.jmx.export.MBeanExporter
 */
public class ManagedResource extends AbstractJmxAttribute {

    private String objectName;

    private boolean log = false;

    private String logFile;

    private String persistPolicy;

    private int persistPeriod = -1;

    private String persistName;

    private String persistLocation;


    /**
     * Set the JMX ObjectName of this managed resource.
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Return the JMX ObjectName of this managed resource.
     */
    public String getObjectName() {
        return this.objectName;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isLog() {
        return this.log;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getLogFile() {
        return this.logFile;
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

    public void setPersistName(String persistName) {
        this.persistName = persistName;
    }

    public String getPersistName() {
        return this.persistName;
    }

    public void setPersistLocation(String persistLocation) {
        this.persistLocation = persistLocation;
    }

    public String getPersistLocation() {
        return this.persistLocation;
    }

}

