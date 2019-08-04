package com.rocket.summer.framework.validation.beanvalidation;

import com.rocket.summer.framework.aop.Pointcut;
import com.rocket.summer.framework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import com.rocket.summer.framework.aop.support.DefaultPointcutAdvisor;
import com.rocket.summer.framework.aop.support.annotation.AnnotationMatchingPointcut;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.beans.factory.config.BeanPostProcessor;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.validation.annotation.Validated;
import org.aopalliance.aop.Advice;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.annotation.Annotation;

/**
 * A convenient {@link BeanPostProcessor} implementation that delegates to a
 * JSR-303 provider for performing method-level validation on annotated methods.
 *
 * <p>Applicable methods have JSR-303 constraint annotations on their parameters
 * and/or on their return value (in the latter case specified at the method level,
 * typically as inline annotation), e.g.:
 *
 * <pre class="code">
 * public @NotNull Object myValidMethod(@NotNull String arg1, @Max(10) int arg2)
 * </pre>
 *
 * <p>Target classes with such annotated methods need to be annotated with Spring's
 * {@link Validated} annotation at the type level, for their methods to be searched for
 * inline constraint annotations. Validation groups can be specified through {@code @Validated}
 * as well. By default, JSR-303 will validate against its default group only.
 *
 * <p>As of Spring 4.0, this functionality requires either a Bean Validation 1.1 provider
 * (such as Hibernate Validator 5.x) or the Bean Validation 1.0 API with Hibernate Validator
 * 4.3. The actual provider will be autodetected and automatically adapted.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see MethodValidationInterceptor
 * @see javax.validation.executable.ExecutableValidator
 * @see org.hibernate.validator.method.MethodValidator
 */
@SuppressWarnings("serial")
public class MethodValidationPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor
        implements InitializingBean {

    private Class<? extends Annotation> validatedAnnotationType = Validated.class;

    private Validator validator;


    /**
     * Set the 'validated' annotation type.
     * The default validated annotation type is the {@link Validated} annotation.
     * <p>This setter property exists so that developers can provide their own
     * (non-Spring-specific) annotation type to indicate that a class is supposed
     * to be validated in the sense of applying method validation.
     * @param validatedAnnotationType the desired annotation type
     */
    public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
        Assert.notNull(validatedAnnotationType, "'validatedAnnotationType' must not be null");
        this.validatedAnnotationType = validatedAnnotationType;
    }

    /**
     * Set the JSR-303 Validator to delegate to for validating methods.
     * <p>Default is the default ValidatorFactory's default Validator.
     */
    public void setValidator(Validator validator) {
        // Unwrap to the native Validator with forExecutables support
        if (validator instanceof LocalValidatorFactoryBean) {
            this.validator = ((LocalValidatorFactoryBean) validator).getValidator();
        }
        else if (validator instanceof SpringValidatorAdapter) {
            this.validator = validator.unwrap(Validator.class);
        }
        else {
            this.validator = validator;
        }
    }

    /**
     * Set the JSR-303 ValidatorFactory to delegate to for validating methods,
     * using its default Validator.
     * <p>Default is the default ValidatorFactory's default Validator.
     * @see javax.validation.ValidatorFactory#getValidator()
     */
    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }


    @Override
    public void afterPropertiesSet() {
        Pointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);
        this.advisor = new DefaultPointcutAdvisor(pointcut, createMethodValidationAdvice(this.validator));
    }

    /**
     * Create AOP advice for method validation purposes, to be applied
     * with a pointcut for the specified 'validated' annotation.
     * @param validator the JSR-303 Validator to delegate to
     * @return the interceptor to use (typically, but not necessarily,
     * a {@link MethodValidationInterceptor} or subclass thereof)
     * @since 4.2
     */
    protected Advice createMethodValidationAdvice(Validator validator) {
        return (validator != null ? new MethodValidationInterceptor(validator) : new MethodValidationInterceptor());
    }

}

