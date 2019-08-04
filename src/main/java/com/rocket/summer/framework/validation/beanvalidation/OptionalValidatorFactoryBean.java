package com.rocket.summer.framework.validation.beanvalidation;

import org.apache.commons.logging.LogFactory;

import javax.validation.ValidationException;

/**
 * {@link LocalValidatorFactoryBean} subclass that simply turns
 * {@link com.rocket.summer.framework.validation.Validator} calls into no-ops
 * in case of no Bean Validation provider being available.
 *
 * <p>This is the actual class used by Spring's MVC configuration namespace,
 * in case of the {@code javax.validation} API being present but no explicit
 * Validator having been configured.
 *
 * @author Juergen Hoeller
 * @since 4.0.1
 */
public class OptionalValidatorFactoryBean extends LocalValidatorFactoryBean {

    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        }
        catch (ValidationException ex) {
            LogFactory.getLog(getClass()).debug("Failed to set up a Bean Validation provider", ex);
        }
    }

}
