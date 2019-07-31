package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.parsing.Location;
import com.rocket.summer.framework.beans.factory.parsing.ProblemReporter;
import com.rocket.summer.framework.core.type.MethodMetadata;

/**
 * @author Chris Beams
 * @since 3.1
 */
abstract class ConfigurationMethod {

    protected final MethodMetadata metadata;

    protected final ConfigurationClass configurationClass;


    public ConfigurationMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
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

    String getFullyQualifiedMethodName() {
        return this.metadata.getDeclaringClassName() + "#" + this.metadata.getMethodName();
    }

    static String getShortMethodName(String fullyQualifiedMethodName) {
        return fullyQualifiedMethodName.substring(fullyQualifiedMethodName.indexOf('#') + 1);
    }

    public void validate(ProblemReporter problemReporter) {
    }


    @Override
    public String toString() {
        return String.format("[%s:name=%s,declaringClass=%s]",
                getClass().getSimpleName(), getMetadata().getMethodName(), getMetadata().getDeclaringClassName());
    }

}

