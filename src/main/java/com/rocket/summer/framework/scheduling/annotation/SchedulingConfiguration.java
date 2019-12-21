package com.rocket.summer.framework.scheduling.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Role;
import com.rocket.summer.framework.scheduling.config.TaskManagementConfigUtils;

/**
 * {@code @Configuration} class that registers a {@link ScheduledAnnotationBeanPostProcessor}
 * bean capable of processing Spring's @{@link Scheduled} annotation.
 *
 * <p>This configuration class is automatically imported when using the
 * {@link EnableScheduling @EnableScheduling} annotation. See
 * {@code @EnableScheduling}'s javadoc for complete usage details.
 *
 * @author Chris Beams
 * @since 3.1
 * @see EnableScheduling
 * @see ScheduledAnnotationBeanPostProcessor
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SchedulingConfiguration {

    @Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new ScheduledAnnotationBeanPostProcessor();
    }

}

