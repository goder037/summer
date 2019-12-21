package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.context.annotation.Condition;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.annotation.Order;
import com.rocket.summer.framework.core.env.PropertyResolver;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Condition} that checks if properties are defined in environment.
 *
 * @author Maciej Walkowiak
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @since 1.1.0
 * @see ConditionalOnProperty
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 40)
class OnPropertyCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        List<AnnotationAttributes> allAnnotationAttributes = annotationAttributesFromMultiValueMap(
                metadata.getAllAnnotationAttributes(
                        ConditionalOnProperty.class.getName()));
        List<ConditionMessage> noMatch = new ArrayList<ConditionMessage>();
        List<ConditionMessage> match = new ArrayList<ConditionMessage>();
        for (AnnotationAttributes annotationAttributes : allAnnotationAttributes) {
            ConditionOutcome outcome = determineOutcome(annotationAttributes,
                    context.getEnvironment());
            (outcome.isMatch() ? match : noMatch).add(outcome.getConditionMessage());
        }
        if (!noMatch.isEmpty()) {
            return ConditionOutcome.noMatch(ConditionMessage.of(noMatch));
        }
        return ConditionOutcome.match(ConditionMessage.of(match));
    }

    private List<AnnotationAttributes> annotationAttributesFromMultiValueMap(
            MultiValueMap<String, Object> multiValueMap) {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, List<Object>> entry : multiValueMap.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                Map<String, Object> map;
                if (i < maps.size()) {
                    map = maps.get(i);
                }
                else {
                    map = new HashMap<String, Object>();
                    maps.add(map);
                }
                map.put(entry.getKey(), entry.getValue().get(i));
            }
        }
        List<AnnotationAttributes> annotationAttributes = new ArrayList<AnnotationAttributes>(
                maps.size());
        for (Map<String, Object> map : maps) {
            annotationAttributes.add(AnnotationAttributes.fromMap(map));
        }
        return annotationAttributes;
    }

    private ConditionOutcome determineOutcome(AnnotationAttributes annotationAttributes,
                                              PropertyResolver resolver) {
        Spec spec = new Spec(annotationAttributes);
        List<String> missingProperties = new ArrayList<String>();
        List<String> nonMatchingProperties = new ArrayList<String>();
        spec.collectProperties(resolver, missingProperties, nonMatchingProperties);
        if (!missingProperties.isEmpty()) {
            return ConditionOutcome.noMatch(
                    ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
                            .didNotFind("property", "properties")
                            .items(ConditionMessage.Style.QUOTE, missingProperties));
        }
        if (!nonMatchingProperties.isEmpty()) {
            return ConditionOutcome.noMatch(
                    ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
                            .found("different value in property",
                                    "different value in properties")
                            .items(ConditionMessage.Style.QUOTE, nonMatchingProperties));
        }
        return ConditionOutcome.match(ConditionMessage
                .forCondition(ConditionalOnProperty.class, spec).because("matched"));
    }

    private static class Spec {

        private final String prefix;

        private final String havingValue;

        private final String[] names;

        private final boolean relaxedNames;

        private final boolean matchIfMissing;

        Spec(AnnotationAttributes annotationAttributes) {
            String prefix = annotationAttributes.getString("prefix").trim();
            if (StringUtils.hasText(prefix) && !prefix.endsWith(".")) {
                prefix = prefix + ".";
            }
            this.prefix = prefix;
            this.havingValue = annotationAttributes.getString("havingValue");
            this.names = getNames(annotationAttributes);
            this.relaxedNames = annotationAttributes.getBoolean("relaxedNames");
            this.matchIfMissing = annotationAttributes.getBoolean("matchIfMissing");
        }

        private String[] getNames(Map<String, Object> annotationAttributes) {
            String[] value = (String[]) annotationAttributes.get("value");
            String[] name = (String[]) annotationAttributes.get("name");
            Assert.state(value.length > 0 || name.length > 0,
                    "The name or value attribute of @ConditionalOnProperty must be specified");
            Assert.state(value.length == 0 || name.length == 0,
                    "The name and value attributes of @ConditionalOnProperty are exclusive");
            return (value.length > 0) ? value : name;
        }

        private void collectProperties(PropertyResolver resolver, List<String> missing,
                                       List<String> nonMatching) {
            if (this.relaxedNames) {
                resolver = new RelaxedPropertyResolver(resolver, this.prefix);
            }
            for (String name : this.names) {
                String key = (this.relaxedNames ? name : this.prefix + name);
                if (resolver.containsProperty(key)) {
                    if (!isMatch(resolver.getProperty(key), this.havingValue)) {
                        nonMatching.add(name);
                    }
                }
                else {
                    if (!this.matchIfMissing) {
                        missing.add(name);
                    }
                }
            }
        }

        private boolean isMatch(String value, String requiredValue) {
            if (StringUtils.hasLength(requiredValue)) {
                return requiredValue.equalsIgnoreCase(value);
            }
            return !"false".equalsIgnoreCase(value);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("(");
            result.append(this.prefix);
            if (this.names.length == 1) {
                result.append(this.names[0]);
            }
            else {
                result.append("[");
                result.append(StringUtils.arrayToCommaDelimitedString(this.names));
                result.append("]");
            }
            if (StringUtils.hasLength(this.havingValue)) {
                result.append("=").append(this.havingValue);
            }
            result.append(")");
            return result.toString();
        }

    }

}

