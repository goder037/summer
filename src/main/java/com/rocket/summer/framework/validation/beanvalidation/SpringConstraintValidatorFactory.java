package com.rocket.summer.framework.validation.beanvalidation;

import com.rocket.summer.framework.beans.factory.config.AutowireCapableBeanFactory;
import com.rocket.summer.framework.util.Assert;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

/**
 * JSR-303 {@link ConstraintValidatorFactory} implementation that delegates to a
 * Spring BeanFactory for creating autowired {@link ConstraintValidator} instances.
 *
 * <p>Note that this class is meant for programmatic use, not for declarative use
 * in a standard {@code validation.xml} file. Consider
 * {@link com.rocket.summer.framework.web.bind.support.SpringWebConstraintValidatorFactory}
 * for declarative use in a web application, e.g. with JAX-RS or JAX-WS.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.beans.factory.config.AutowireCapableBeanFactory#createBean(Class)
 * @see com.rocket.summer.framework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {

    private final AutowireCapableBeanFactory beanFactory;


    /**
     * Create a new SpringConstraintValidatorFactory for the given BeanFactory.
     * @param beanFactory the target BeanFactory
     */
    public SpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }


    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return this.beanFactory.createBean(key);
    }

    // Bean Validation 1.1 releaseInstance method
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        this.beanFactory.destroyBean(instance);
    }

}

