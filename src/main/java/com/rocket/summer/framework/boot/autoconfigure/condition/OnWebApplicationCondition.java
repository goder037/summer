package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.context.annotation.Condition;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.Order;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.web.context.WebApplicationContext;
import com.rocket.summer.framework.web.context.support.StandardServletEnvironment;

/**
 * {@link Condition} that checks for the presence or absence of
 * {@link WebApplicationContext}.
 *
 * @author Dave Syer
 * @see ConditionalOnWebApplication
 * @see ConditionalOnNotWebApplication
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
class OnWebApplicationCondition extends SpringBootCondition {

    private static final String WEB_CONTEXT_CLASS = "org.springframework.web.context."
            + "support.GenericWebApplicationContext";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
        boolean required = metadata
                .isAnnotated(ConditionalOnWebApplication.class.getName());
        ConditionOutcome outcome = isWebApplication(context, metadata, required);
        if (required && !outcome.isMatch()) {
            return ConditionOutcome.noMatch(outcome.getConditionMessage());
        }
        if (!required && outcome.isMatch()) {
            return ConditionOutcome.noMatch(outcome.getConditionMessage());
        }
        return ConditionOutcome.match(outcome.getConditionMessage());
    }

    private ConditionOutcome isWebApplication(ConditionContext context,
                                              AnnotatedTypeMetadata metadata, boolean required) {
        ConditionMessage.Builder message = ConditionMessage.forCondition(
                ConditionalOnWebApplication.class, required ? "(required)" : "");
        if (!ClassUtils.isPresent(WEB_CONTEXT_CLASS, context.getClassLoader())) {
            return ConditionOutcome
                    .noMatch(message.didNotFind("web application classes").atAll());
        }
        if (context.getBeanFactory() != null) {
            String[] scopes = context.getBeanFactory().getRegisteredScopeNames();
            if (ObjectUtils.containsElement(scopes, "session")) {
                return ConditionOutcome.match(message.foundExactly("'session' scope"));
            }
        }
        if (context.getEnvironment() instanceof StandardServletEnvironment) {
            return ConditionOutcome
                    .match(message.foundExactly("StandardServletEnvironment"));
        }
        if (context.getResourceLoader() instanceof WebApplicationContext) {
            return ConditionOutcome.match(message.foundExactly("WebApplicationContext"));
        }
        return ConditionOutcome.noMatch(message.because("not a web application"));
    }

}

