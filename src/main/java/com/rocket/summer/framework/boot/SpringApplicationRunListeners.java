package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A collection of {@link SpringApplicationRunListener}.
 *
 * @author Phillip Webb
 */
class SpringApplicationRunListeners {

    private final Log log;

    private final List<SpringApplicationRunListener> listeners;

    SpringApplicationRunListeners(Log log,
                                  Collection<? extends SpringApplicationRunListener> listeners) {
        this.log = log;
        this.listeners = new ArrayList<SpringApplicationRunListener>(listeners);
    }

    public void starting() {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.starting();
        }
    }

    public void environmentPrepared(ConfigurableEnvironment environment) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.environmentPrepared(environment);
        }
    }

    public void contextPrepared(ConfigurableApplicationContext context) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.contextPrepared(context);
        }
    }

    public void contextLoaded(ConfigurableApplicationContext context) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.contextLoaded(context);
        }
    }

    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        for (SpringApplicationRunListener listener : this.listeners) {
            callFinishedListener(listener, context, exception);
        }
    }

    private void callFinishedListener(SpringApplicationRunListener listener,
                                      ConfigurableApplicationContext context, Throwable exception) {
        try {
            listener.finished(context, exception);
        }
        catch (Throwable ex) {
            if (exception == null) {
                ReflectionUtils.rethrowRuntimeException(ex);
            }
            if (this.log.isDebugEnabled()) {
                this.log.error("Error handling failed", ex);
            }
            else {
                String message = ex.getMessage();
                message = (message != null) ? message : "no error message";
                this.log.warn("Error handling failed (" + message + ")");
            }
        }
    }

}

