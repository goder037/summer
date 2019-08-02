package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.parsing.Problem;
import com.rocket.summer.framework.beans.factory.parsing.ProblemReporter;
import com.rocket.summer.framework.core.type.MethodMetadata;

/**
 * Represents a {@link Configuration} class method marked with the
 * {@link Bean} annotation.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see ConfigurationClass
 * @see ConfigurationClassParser
 * @see ConfigurationClassBeanDefinitionReader
 */
final class BeanMethod extends ConfigurationMethod {

    public BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        super(metadata, configurationClass);
    }

    @Override
    public void validate(ProblemReporter problemReporter) {
        if (getMetadata().isStatic()) {
            // static @Bean methods have no constraints to validate -> return immediately
            return;
        }

        if (this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName())) {
            if (!getMetadata().isOverridable()) {
                // instance @Bean methods within @Configuration classes must be overridable to accommodate CGLIB
                problemReporter.error(new NonOverridableMethodError());
            }
        }
    }


    private class NonOverridableMethodError extends Problem {

        public NonOverridableMethodError() {
            super(String.format("@Bean method '%s' must not be private or final; change the method's modifiers to continue",
                    getMetadata().getMethodName()), getResourceLocation());
        }
    }
}

