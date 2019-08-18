package com.rocket.summer.framework.scheduling.annotation;

import java.util.Collection;
import java.util.concurrent.Executor;

import com.rocket.summer.framework.aop.interceptor.AsyncUncaughtExceptionHandler;
import com.rocket.summer.framework.beans.factory.annotation.Autowired;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.ImportAware;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * Abstract base {@code Configuration} class providing common structure for enabling
 * Spring's asynchronous method execution capability.
 *
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 * @see EnableAsync
 */
@Configuration
public abstract class AbstractAsyncConfiguration implements ImportAware {

    protected AnnotationAttributes enableAsync;

    protected Executor executor;

    protected AsyncUncaughtExceptionHandler exceptionHandler;


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableAsync = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableAsync.class.getName(), false));
        if (this.enableAsync == null) {
            throw new IllegalArgumentException(
                    "@EnableAsync is not present on importing class " + importMetadata.getClassName());
        }
    }

    /**
     * Collect any {@link AsyncConfigurer} beans through autowiring.
     */
    @Autowired(required = false)
    void setConfigurers(Collection<AsyncConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException("Only one AsyncConfigurer may exist");
        }
        AsyncConfigurer configurer = configurers.iterator().next();
        this.executor = configurer.getAsyncExecutor();
        this.exceptionHandler = configurer.getAsyncUncaughtExceptionHandler();
    }

}

