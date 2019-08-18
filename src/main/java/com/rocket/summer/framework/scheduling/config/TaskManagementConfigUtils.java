package com.rocket.summer.framework.scheduling.config;

/**
 * Configuration constants for internal sharing across subpackages.
 *
 * @author Juergen Hoeller
 * @since 4.1
 */
public class TaskManagementConfigUtils {

    /**
     * The bean name of the internally managed Scheduled annotation processor.
     */
    public static final String SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.rocket.summer.framework.context.annotation.internalScheduledAnnotationProcessor";

    /**
     * The bean name of the internally managed Async annotation processor.
     */
    public static final String ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.rocket.summer.framework.context.annotation.internalAsyncAnnotationProcessor";

    /**
     * The bean name of the internally managed AspectJ async execution aspect.
     */
    public static final String ASYNC_EXECUTION_ASPECT_BEAN_NAME =
            "com.rocket.summer.framework.scheduling.config.internalAsyncExecutionAspect";

}

