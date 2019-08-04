package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * An application process ID.
 *
 * @author Phillip Webb
 */
public class ApplicationPid {

    private final String pid;

    public ApplicationPid() {
        this.pid = getPid();
    }

    protected ApplicationPid(String pid) {
        this.pid = pid;
    }

    private String getPid() {
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        }
        catch (Throwable ex) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj instanceof ApplicationPid) {
            return ObjectUtils.nullSafeEquals(this.pid, ((ApplicationPid) obj).pid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.pid);
    }

    @Override
    public String toString() {
        return (this.pid != null) ? this.pid : "???";
    }

    /**
     * Write the PID to the specified file.
     * @param file the PID file
     * @throws IllegalStateException if no PID is available.
     * @throws IOException if the file cannot be written
     */
    public void write(File file) throws IOException {
        Assert.state(this.pid != null, "No PID available");
        createParentFolder(file);
        FileWriter writer = new FileWriter(file);
        try {
            writer.append(this.pid);
        }
        finally {
            writer.close();
        }
    }

    private void createParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }

}
