package com.rocket.summer.framework.boot.context.embedded.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.StandardSessionIdGenerator;

/**
 * A specialization of {@link StandardSessionIdGenerator} that initializes
 * {@code SecureRandom} lazily.
 *
 * @author Andy Wilkinson
 */
class LazySessionIdGenerator extends StandardSessionIdGenerator {

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

}
