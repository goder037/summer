package com.rocket.summer.framework.context;

import com.rocket.summer.framework.beans.factory.Aware;
import com.rocket.summer.framework.core.env.Environment;

/**
 * Interface to be implemented by any bean that wishes to be notified
 * of the {@link Environment} that it runs in.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface EnvironmentAware extends Aware {

    /**
     * Set the {@code Environment} that this object runs in.
     */
    void setEnvironment(Environment environment);

}
