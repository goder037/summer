package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionMessage;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionOutcome;
import com.rocket.summer.framework.boot.autoconfigure.condition.SpringBootCondition;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.context.annotation.Condition;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.core.env.PropertyResolver;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * {@link Condition} that checks whether or not the Spring resource handling chain is
 * enabled.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @see ConditionalOnEnabledResourceChain
 */
class OnEnabledResourceChainCondition extends SpringBootCondition {

    private static final String WEBJAR_ASSET_LOCATOR = "org.webjars.WebJarAssetLocator";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        ConfigurableEnvironment environment = (ConfigurableEnvironment) context
                .getEnvironment();
        boolean fixed = getEnabledProperty(environment, "strategy.fixed.", false);
        boolean content = getEnabledProperty(environment, "strategy.content.", false);
        Boolean chain = getEnabledProperty(environment, "", null);
        Boolean match = ResourceProperties.Chain.getEnabled(fixed, content, chain);
        ConditionMessage.Builder message = ConditionMessage
                .forCondition(ConditionalOnEnabledResourceChain.class);
        if (match == null) {
            if (ClassUtils.isPresent(WEBJAR_ASSET_LOCATOR, getClass().getClassLoader())) {
                return ConditionOutcome
                        .match(message.found("class").items(WEBJAR_ASSET_LOCATOR));
            }
            return ConditionOutcome
                    .noMatch(message.didNotFind("class").items(WEBJAR_ASSET_LOCATOR));
        }
        if (match) {
            return ConditionOutcome.match(message.because("enabled"));
        }
        return ConditionOutcome.noMatch(message.because("disabled"));
    }

    private Boolean getEnabledProperty(ConfigurableEnvironment environment, String key,
                                       Boolean defaultValue) {
        PropertyResolver resolver = new RelaxedPropertyResolver(environment,
                "spring.resources.chain." + key);
        return resolver.getProperty("enabled", Boolean.class, defaultValue);
    }

}

