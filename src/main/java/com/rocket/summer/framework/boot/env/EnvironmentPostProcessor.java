package com.rocket.summer.framework.boot.env;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.core.env.Environment;

/**
 * Allows for customization of the application's {@link Environment} prior to the
 * application context being refreshed.
 * <p>
 * EnvironmentPostProcessor implementations have to be registered in
 * {@code META-INF/spring.factories}, using the fully qualified name of this class as the
 * key.
 * <p>
 * {@code EnvironmentPostProcessor} processors are encouraged to detect whether Spring's
 * {@link com.rocket.summer.framework.core.Ordered Ordered} interface has been implemented or if
 * the {@link com.rocket.summer.framework.core.annotation.Order @Order} annotation is present and
 * to sort instances accordingly if so prior to invocation.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @since 1.3.0
 */
public interface EnvironmentPostProcessor {

    /**
     * Post-process the given {@code environment}.
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     */
    void postProcessEnvironment(ConfigurableEnvironment environment,
                                SpringApplication application);

}
