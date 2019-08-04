package com.rocket.summer.framework.boot.autoconfigure.validation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnResource;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.boot.validation.MessageInterpolatorFactory;
import com.rocket.summer.framework.context.annotation.*;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.validation.beanvalidation.LocalValidatorFactoryBean;
import com.rocket.summer.framework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

/**
 * {@link EnableAutoConfiguration Auto-configuration} to configure the validation
 * infrastructure.
 *
 * @author Stephane Nicoll
 * @since 1.5.0
 */
@Configuration
@ConditionalOnClass(ExecutableValidator.class)
@ConditionalOnResource(
        resources = "classpath:META-INF/services/javax.validation.spi.ValidationProvider")
@Import(PrimaryDefaultValidatorPostProcessor.class)
public class ValidationAutoConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(Validator.class)
    public static LocalValidatorFactoryBean defaultValidator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        return factoryBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public static MethodValidationPostProcessor methodValidationPostProcessor(
            Environment environment, @Lazy Validator validator) {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setProxyTargetClass(determineProxyTargetClass(environment));
        processor.setValidator(validator);
        return processor;
    }

    private static boolean determineProxyTargetClass(Environment environment) {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment,
                "spring.aop.");
        Boolean value = resolver.getProperty("proxyTargetClass", Boolean.class);
        return (value != null) ? value : true;
    }

}
