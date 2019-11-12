package com.rocket.summer.framework.boot.autoconfigure.cache;

import java.util.Locale;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionMessage;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionOutcome;
import com.rocket.summer.framework.boot.autoconfigure.condition.SpringBootCondition;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.ClassMetadata;

/**
 * General cache condition used with all cache configuration classes.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @since 1.3.0
 */
class CacheCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            sourceClass = ((ClassMetadata) metadata).getClassName();
        }
        ConditionMessage.Builder message = ConditionMessage.forCondition("Cache",
                sourceClass);
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                context.getEnvironment(), "spring.cache.");
        if (!resolver.containsProperty("type")) {
            return ConditionOutcome.match(message.because("automatic cache type"));
        }
        CacheType cacheType = CacheConfigurations
                .getType(((AnnotationMetadata) metadata).getClassName());
        String value = resolver.getProperty("type").replace('-', '_')
                .toUpperCase(Locale.ENGLISH);
        if (value.equals(cacheType.name())) {
            return ConditionOutcome.match(message.because(value + " cache type"));
        }
        return ConditionOutcome.noMatch(message.because(value + " cache type"));
    }

}

