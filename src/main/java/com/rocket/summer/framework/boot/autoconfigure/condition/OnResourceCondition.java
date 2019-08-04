package com.rocket.summer.framework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.List;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionMessage.Style;
import com.rocket.summer.framework.context.annotation.Condition;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.Order;
import com.rocket.summer.framework.core.io.DefaultResourceLoader;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.MultiValueMap;

/**
 * {@link Condition} that checks for specific resources.
 *
 * @author Dave Syer
 * @see ConditionalOnResource
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
class OnResourceCondition extends SpringBootCondition {

    private final ResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata
                .getAllAnnotationAttributes(ConditionalOnResource.class.getName(), true);
        ResourceLoader loader = (context.getResourceLoader() != null)
                ? context.getResourceLoader() : this.defaultResourceLoader;
        List<String> locations = new ArrayList<String>();
        collectValues(locations, attributes.get("resources"));
        Assert.isTrue(!locations.isEmpty(),
                "@ConditionalOnResource annotations must specify at "
                        + "least one resource location");
        List<String> missing = new ArrayList<String>();
        for (String location : locations) {
            String resource = context.getEnvironment().resolvePlaceholders(location);
            if (!loader.getResource(resource).exists()) {
                missing.add(location);
            }
        }
        if (!missing.isEmpty()) {
            return ConditionOutcome.noMatch(ConditionMessage
                    .forCondition(ConditionalOnResource.class)
                    .didNotFind("resource", "resources").items(Style.QUOTE, missing));
        }
        return ConditionOutcome
                .match(ConditionMessage.forCondition(ConditionalOnResource.class)
                        .found("location", "locations").items(locations));
    }

    private void collectValues(List<String> names, List<Object> values) {
        for (Object value : values) {
            for (Object item : (Object[]) value) {
                names.add((String) item);
            }
        }
    }

}

