package com.rocket.summer.framework.context.support;

/**
 * MBean operation interface for the {@link LiveBeansView} feature.
 *
 * @author Juergen Hoeller
 * @since 3.2
 */
public interface LiveBeansViewMBean {

    /**
     * Generate a JSON snapshot of current beans and their dependencies.
     */
    String getSnapshotAsJson();

}
