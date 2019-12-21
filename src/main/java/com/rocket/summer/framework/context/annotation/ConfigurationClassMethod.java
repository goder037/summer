package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.parsing.Location;
import com.rocket.summer.framework.beans.factory.parsing.Problem;
import com.rocket.summer.framework.beans.factory.parsing.ProblemReporter;
import com.rocket.summer.framework.core.type.MethodMetadata;

/**
 * Represents a {@link Configuration} class method marked with the {@link Bean} annotation.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see ConfigurationClass
 * @see ConfigurationClassParser
 * @see ConfigurationClassBeanDefinitionReader
 */
final class ConfigurationClassMethod {

    private final MethodMetadata metadata;

    private final ConfigurationClass configurationClass;


    public ConfigurationClassMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        this.metadata = metadata;
        this.configurationClass = configurationClass;
    }

    public MethodMetadata getMetadata() {
        return this.metadata;
    }

    public ConfigurationClass getConfigurationClass() {
        return this.configurationClass;
    }

    public Location getResourceLocation() {
        return new Location(this.configurationClass.getResource(), this.metadata);
    }

    public void validate(ProblemReporter problemReporter) {
        if (this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName())) {
            if (!getMetadata().isOverridable()) {
                problemReporter.error(new NonOverridableMethodError());
            }
        }
        else {
            if (getMetadata().isStatic()) {
                problemReporter.error(new StaticMethodError());
            }
        }
    }

    @Override
    public String toString() {
        return String.format("[%s:name=%s,declaringClass=%s]",
                this.getClass().getSimpleName(), this.getMetadata().getMethodName(), this.getMetadata().getDeclaringClassName());
    }


    /**
     * {@link Bean} methods must be overridable in order to accommodate CGLIB.
     */
    private class NonOverridableMethodError extends Problem {

        public NonOverridableMethodError() {
            super(String.format("Method '%s' must not be private, final or static; change the method's modifiers to continue",
                    getMetadata().getMethodName()), getResourceLocation());
        }
    }


    /**
     * {@link Bean} methods must at least not be static in the non-CGLIB case.
     */
    private class StaticMethodError extends Problem {

        public StaticMethodError() {
            super(String.format("Method '%s' must not be static; remove the method's static modifier to continue",
                    getMetadata().getMethodName()), getResourceLocation());
        }
    }

}
