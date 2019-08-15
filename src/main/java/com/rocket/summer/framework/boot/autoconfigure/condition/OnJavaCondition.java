package com.rocket.summer.framework.boot.autoconfigure.condition;

import java.util.Map;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnJava.JavaVersion;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnJava.Range;
import com.rocket.summer.framework.context.annotation.Condition;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.Order;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;

/**
 * {@link Condition} that checks for a required version of Java.
 *
 * @author Oliver Gierke
 * @author Phillip Webb
 * @see ConditionalOnJava
 * @since 1.1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
class OnJavaCondition extends SpringBootCondition {

    private static final JavaVersion JVM_VERSION = JavaVersion.getJavaVersion();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata
                .getAnnotationAttributes(ConditionalOnJava.class.getName());
        Range range = (Range) attributes.get("range");
        JavaVersion version = (JavaVersion) attributes.get("value");
        return getMatchOutcome(range, JVM_VERSION, version);
    }

    protected ConditionOutcome getMatchOutcome(Range range, JavaVersion runningVersion,
                                               JavaVersion version) {
        boolean match = runningVersion.isWithin(range, version);
        String expected = String.format(
                (range != Range.EQUAL_OR_NEWER) ? "(older than %s)" : "(%s or newer)",
                version);
        ConditionMessage message = ConditionMessage
                .forCondition(ConditionalOnJava.class, expected)
                .foundExactly(runningVersion);
        return new ConditionOutcome(match, message);
    }

}

