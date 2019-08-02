package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.context.annotation.Condition;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.ClassMetadata;
import com.rocket.summer.framework.core.type.MethodMetadata;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base of all {@link Condition} implementations used with Spring Boot. Provides sensible
 * logging to help the user diagnose what classes are loaded.
 *
 * @author Phillip Webb
 * @author Greg Turnquist
 */
public abstract class SpringBootCondition implements Condition {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public final boolean matches(ConditionContext context,
                                 AnnotatedTypeMetadata metadata) {
        String classOrMethodName = getClassOrMethodName(metadata);
        try {
            ConditionOutcome outcome = getMatchOutcome(context, metadata);
            logOutcome(classOrMethodName, outcome);
            recordEvaluation(context, classOrMethodName, outcome);
            return outcome.isMatch();
        }
        catch (NoClassDefFoundError ex) {
            throw new IllegalStateException(
                    "Could not evaluate condition on " + classOrMethodName + " due to "
                            + ex.getMessage() + " not "
                            + "found. Make sure your own configuration does not rely on "
                            + "that class. This can also happen if you are "
                            + "@ComponentScanning a springframework package (e.g. if you "
                            + "put a @ComponentScan in the default package by mistake)",
                    ex);
        }
        catch (RuntimeException ex) {
            throw new IllegalStateException(
                    "Error processing condition on " + getName(metadata), ex);
        }
    }

    private String getName(AnnotatedTypeMetadata metadata) {
        if (metadata instanceof AnnotationMetadata) {
            return ((AnnotationMetadata) metadata).getClassName();
        }
        if (metadata instanceof MethodMetadata) {
            MethodMetadata methodMetadata = (MethodMetadata) metadata;
            return methodMetadata.getDeclaringClassName() + "."
                    + methodMetadata.getMethodName();
        }
        return metadata.toString();
    }

    private static String getClassOrMethodName(AnnotatedTypeMetadata metadata) {
        if (metadata instanceof ClassMetadata) {
            ClassMetadata classMetadata = (ClassMetadata) metadata;
            return classMetadata.getClassName();
        }
        MethodMetadata methodMetadata = (MethodMetadata) metadata;
        return methodMetadata.getDeclaringClassName() + "#"
                + methodMetadata.getMethodName();
    }

    protected final void logOutcome(String classOrMethodName, ConditionOutcome outcome) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(getLogMessage(classOrMethodName, outcome));
        }
    }

    private StringBuilder getLogMessage(String classOrMethodName,
                                        ConditionOutcome outcome) {
        StringBuilder message = new StringBuilder();
        message.append("Condition ");
        message.append(ClassUtils.getShortName(getClass()));
        message.append(" on ");
        message.append(classOrMethodName);
        message.append(outcome.isMatch() ? " matched" : " did not match");
        if (StringUtils.hasLength(outcome.getMessage())) {
            message.append(" due to ");
            message.append(outcome.getMessage());
        }
        return message;
    }

    private void recordEvaluation(ConditionContext context, String classOrMethodName,
                                  ConditionOutcome outcome) {
        if (context.getBeanFactory() != null) {
            ConditionEvaluationReport.get(context.getBeanFactory())
                    .recordConditionEvaluation(classOrMethodName, this, outcome);
        }
    }

    /**
     * Determine the outcome of the match along with suitable log output.
     * @param context the condition context
     * @param metadata the annotation metadata
     * @return the condition outcome
     */
    public abstract ConditionOutcome getMatchOutcome(ConditionContext context,
                                                     AnnotatedTypeMetadata metadata);

    /**
     * Return true if any of the specified conditions match.
     * @param context the context
     * @param metadata the annotation meta-data
     * @param conditions conditions to test
     * @return {@code true} if any condition matches.
     */
    protected final boolean anyMatches(ConditionContext context,
                                       AnnotatedTypeMetadata metadata, Condition... conditions) {
        for (Condition condition : conditions) {
            if (matches(context, metadata, condition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if any of the specified condition matches.
     * @param context the context
     * @param metadata the annotation meta-data
     * @param condition condition to test
     * @return {@code true} if the condition matches.
     */
    protected final boolean matches(ConditionContext context,
                                    AnnotatedTypeMetadata metadata, Condition condition) {
        if (condition instanceof SpringBootCondition) {
            return ((SpringBootCondition) condition).getMatchOutcome(context, metadata)
                    .isMatch();
        }
        return condition.matches(context, metadata);
    }

}

