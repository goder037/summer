package com.rocket.summer.framework.boot.autoconfigure.condition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.context.annotation.Condition;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.ConfigurationCondition;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.core.type.classreading.SimpleMetadataReaderFactory;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;

/**
 * Abstract base class for nested conditions.
 *
 * @author Phillip Webb
 */
abstract class AbstractNestedCondition extends SpringBootCondition
        implements ConfigurationCondition {

    private final ConfigurationPhase configurationPhase;

    AbstractNestedCondition(ConfigurationPhase configurationPhase) {
        Assert.notNull(configurationPhase, "ConfigurationPhase must not be null");
        this.configurationPhase = configurationPhase;
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return this.configurationPhase;
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        String className = getClass().getName();
        MemberConditions memberConditions = new MemberConditions(context, className);
        MemberMatchOutcomes memberOutcomes = new MemberMatchOutcomes(memberConditions);
        return getFinalMatchOutcome(memberOutcomes);
    }

    protected abstract ConditionOutcome getFinalMatchOutcome(
            MemberMatchOutcomes memberOutcomes);

    protected static class MemberMatchOutcomes {

        private final List<ConditionOutcome> all;

        private final List<ConditionOutcome> matches;

        private final List<ConditionOutcome> nonMatches;

        public MemberMatchOutcomes(MemberConditions memberConditions) {
            this.all = Collections.unmodifiableList(memberConditions.getMatchOutcomes());
            List<ConditionOutcome> matches = new ArrayList<ConditionOutcome>();
            List<ConditionOutcome> nonMatches = new ArrayList<ConditionOutcome>();
            for (ConditionOutcome outcome : this.all) {
                (outcome.isMatch() ? matches : nonMatches).add(outcome);
            }
            this.matches = Collections.unmodifiableList(matches);
            this.nonMatches = Collections.unmodifiableList(nonMatches);
        }

        public List<ConditionOutcome> getAll() {
            return this.all;
        }

        public List<ConditionOutcome> getMatches() {
            return this.matches;
        }

        public List<ConditionOutcome> getNonMatches() {
            return this.nonMatches;
        }

    }

    private static class MemberConditions {

        private final ConditionContext context;

        private final MetadataReaderFactory readerFactory;

        private final Map<AnnotationMetadata, List<Condition>> memberConditions;

        MemberConditions(ConditionContext context, String className) {
            this.context = context;
            this.readerFactory = new SimpleMetadataReaderFactory(
                    context.getResourceLoader());
            String[] members = getMetadata(className).getMemberClassNames();
            this.memberConditions = getMemberConditions(members);
        }

        private Map<AnnotationMetadata, List<Condition>> getMemberConditions(
                String[] members) {
            MultiValueMap<AnnotationMetadata, Condition> memberConditions = new LinkedMultiValueMap<AnnotationMetadata, Condition>();
            for (String member : members) {
                AnnotationMetadata metadata = getMetadata(member);
                for (String[] conditionClasses : getConditionClasses(metadata)) {
                    for (String conditionClass : conditionClasses) {
                        Condition condition = getCondition(conditionClass);
                        memberConditions.add(metadata, condition);
                    }
                }
            }
            return Collections.unmodifiableMap(memberConditions);
        }

        private AnnotationMetadata getMetadata(String className) {
            try {
                return this.readerFactory.getMetadataReader(className)
                        .getAnnotationMetadata();
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @SuppressWarnings("unchecked")
        private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
            MultiValueMap<String, Object> attributes = metadata
                    .getAllAnnotationAttributes(Conditional.class.getName(), true);
            Object values = (attributes != null) ? attributes.get("value") : null;
            return (List<String[]>) ((values != null) ? values : Collections.emptyList());
        }

        private Condition getCondition(String conditionClassName) {
            Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName,
                    this.context.getClassLoader());
            return (Condition) BeanUtils.instantiateClass(conditionClass);
        }

        public List<ConditionOutcome> getMatchOutcomes() {
            List<ConditionOutcome> outcomes = new ArrayList<ConditionOutcome>();
            for (Map.Entry<AnnotationMetadata, List<Condition>> entry : this.memberConditions
                    .entrySet()) {
                AnnotationMetadata metadata = entry.getKey();
                List<Condition> conditions = entry.getValue();
                outcomes.add(new MemberOutcomes(this.context, metadata, conditions)
                        .getUltimateOutcome());
            }
            return Collections.unmodifiableList(outcomes);
        }

    }

    private static class MemberOutcomes {

        private final ConditionContext context;

        private final AnnotationMetadata metadata;

        private final List<ConditionOutcome> outcomes;

        MemberOutcomes(ConditionContext context, AnnotationMetadata metadata,
                       List<Condition> conditions) {
            this.context = context;
            this.metadata = metadata;
            this.outcomes = new ArrayList<ConditionOutcome>(conditions.size());
            for (Condition condition : conditions) {
                this.outcomes.add(getConditionOutcome(metadata, condition));
            }
        }

        private ConditionOutcome getConditionOutcome(AnnotationMetadata metadata,
                                                     Condition condition) {
            if (condition instanceof SpringBootCondition) {
                return ((SpringBootCondition) condition).getMatchOutcome(this.context,
                        metadata);
            }
            return new ConditionOutcome(condition.matches(this.context, metadata),
                    ConditionMessage.empty());
        }

        public ConditionOutcome getUltimateOutcome() {
            ConditionMessage.Builder message = ConditionMessage
                    .forCondition("NestedCondition on "
                            + ClassUtils.getShortName(this.metadata.getClassName()));
            if (this.outcomes.size() == 1) {
                ConditionOutcome outcome = this.outcomes.get(0);
                return new ConditionOutcome(outcome.isMatch(),
                        message.because(outcome.getMessage()));
            }
            List<ConditionOutcome> match = new ArrayList<ConditionOutcome>();
            List<ConditionOutcome> nonMatch = new ArrayList<ConditionOutcome>();
            for (ConditionOutcome outcome : this.outcomes) {
                (outcome.isMatch() ? match : nonMatch).add(outcome);
            }
            if (nonMatch.isEmpty()) {
                return ConditionOutcome
                        .match(message.found("matching nested conditions").items(match));
            }
            return ConditionOutcome.noMatch(
                    message.found("non-matching nested conditions").items(nonMatch));
        }

    }

}

