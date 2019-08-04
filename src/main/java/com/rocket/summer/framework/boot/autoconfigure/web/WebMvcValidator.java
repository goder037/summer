package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.boot.validation.MessageInterpolatorFactory;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.validation.Errors;
import com.rocket.summer.framework.validation.SmartValidator;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.validation.beanvalidation.OptionalValidatorFactoryBean;
import com.rocket.summer.framework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * A {@link SmartValidator} exposed as a bean for WebMvc use. Wraps existing
 * {@link SpringValidatorAdapter} instances so that only the Spring's {@link Validator}
 * type is exposed. This prevents such a bean to expose both the Spring and JSR-303
 * validator contract at the same time.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
class WebMvcValidator implements SmartValidator, ApplicationContextAware,
        InitializingBean, DisposableBean {

    private final SpringValidatorAdapter target;

    private final boolean existingBean;

    WebMvcValidator(SpringValidatorAdapter target, boolean existingBean) {
        this.target = target;
        this.existingBean = existingBean;
    }

    SpringValidatorAdapter getTarget() {
        return this.target;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.target.supports(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        this.target.validate(target, errors);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        this.target.validate(target, errors, validationHints);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        if (!this.existingBean && this.target instanceof ApplicationContextAware) {
            ((ApplicationContextAware) this.target)
                    .setApplicationContext(applicationContext);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!this.existingBean && this.target instanceof InitializingBean) {
            ((InitializingBean) this.target).afterPropertiesSet();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (!this.existingBean && this.target instanceof DisposableBean) {
            ((DisposableBean) this.target).destroy();
        }
    }

    public static Validator get(ApplicationContext applicationContext,
                                Validator validator) {
        if (validator != null) {
            return wrap(validator, false);
        }
        return getExistingOrCreate(applicationContext);
    }

    private static Validator getExistingOrCreate(ApplicationContext applicationContext) {
        Validator existing = getExisting(applicationContext);
        if (existing != null) {
            return wrap(existing, true);
        }
        return create();
    }

    private static Validator getExisting(ApplicationContext applicationContext) {
        try {
            javax.validation.Validator validator = applicationContext
                    .getBean(javax.validation.Validator.class);
            if (validator instanceof Validator) {
                return (Validator) validator;
            }
            return new SpringValidatorAdapter(validator);
        }
        catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    private static Validator create() {
        OptionalValidatorFactoryBean validator = new OptionalValidatorFactoryBean();
        validator.setMessageInterpolator(new MessageInterpolatorFactory().getObject());
        return wrap(validator, false);
    }

    private static Validator wrap(Validator validator, boolean existingBean) {
        if (validator instanceof javax.validation.Validator) {
            if (validator instanceof SpringValidatorAdapter) {
                return new WebMvcValidator((SpringValidatorAdapter) validator,
                        existingBean);
            }
            return new WebMvcValidator(
                    new SpringValidatorAdapter((javax.validation.Validator) validator),
                    existingBean);
        }
        return validator;
    }

}

