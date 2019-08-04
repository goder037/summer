package com.rocket.summer.framework.boot.autoconfigure.validation;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.BeanFactoryUtils;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Enable the {@code Primary} flag on the auto-configured validator if necessary.
 * <p>
 * As {@link LocalValidatorFactoryBean} exposes 3 validator related contracts and we're
 * only checking for the absence {@link javax.validation.Validator}, we should flag the
 * auto-configured validator as primary only if no Spring's {@link Validator} is flagged
 * as primary.
 *
 * @author Stephane Nicoll
 */
class PrimaryDefaultValidatorPostProcessor
        implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

    /**
     * The bean name of the auto-configured Validator.
     */
    private static final String VALIDATOR_BEAN_NAME = "defaultValidator";

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        BeanDefinition definition = getAutoConfiguredValidator(registry);
        if (definition != null) {
            definition.setPrimary(!hasPrimarySpringValidator(registry));
        }
    }

    private BeanDefinition getAutoConfiguredValidator(BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(VALIDATOR_BEAN_NAME)) {
            BeanDefinition definition = registry.getBeanDefinition(VALIDATOR_BEAN_NAME);
            if (definition.getRole() == BeanDefinition.ROLE_INFRASTRUCTURE && isTypeMatch(
                    VALIDATOR_BEAN_NAME, LocalValidatorFactoryBean.class)) {
                return definition;
            }
        }
        return null;
    }

    private boolean isTypeMatch(String name, Class<?> type) {
        return this.beanFactory != null && this.beanFactory.isTypeMatch(name, type);
    }

    private boolean hasPrimarySpringValidator(BeanDefinitionRegistry registry) {
        String[] validatorBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                this.beanFactory, Validator.class, false, false);
        for (String validatorBean : validatorBeans) {
            BeanDefinition definition = registry.getBeanDefinition(validatorBean);
            if (definition != null && definition.isPrimary()) {
                return true;
            }
        }
        return false;
    }

}
