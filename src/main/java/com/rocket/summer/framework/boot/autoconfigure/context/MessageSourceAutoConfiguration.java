package com.rocket.summer.framework.boot.autoconfigure.context;

import java.nio.charset.Charset;

import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureOrder;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionMessage;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionOutcome;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.SearchStrategy;
import com.rocket.summer.framework.boot.autoconfigure.condition.SpringBootCondition;
import com.rocket.summer.framework.boot.autoconfigure.context.MessageSourceAutoConfiguration.ResourceBundleCondition;
import com.rocket.summer.framework.boot.context.properties.ConfigurationProperties;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.context.MessageSource;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.support.ResourceBundleMessageSource;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.support.PathMatchingResourcePatternResolver;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.util.ConcurrentReferenceHashMap;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link MessageSource}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Eddú Meléndez
 */
@Configuration
@ConditionalOnMissingBean(value = MessageSource.class, search = SearchStrategy.CURRENT)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Conditional(ResourceBundleCondition.class)
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.messages")
public class MessageSourceAutoConfiguration {

    private static final Resource[] NO_RESOURCES = {};

    /**
     * Comma-separated list of basenames (essentially a fully-qualified classpath
     * location), each following the ResourceBundle convention with relaxed support for
     * slash based locations. If it doesn't contain a package qualifier (such as
     * "org.mypackage"), it will be resolved from the classpath root.
     */
    private String basename = "messages";

    /**
     * Message bundles encoding.
     */
    private Charset encoding = Charset.forName("UTF-8");

    /**
     * Loaded resource bundle files cache expiration, in seconds. When set to -1, bundles
     * are cached forever.
     */
    private int cacheSeconds = -1;

    /**
     * Set whether to fall back to the system Locale if no files for a specific Locale
     * have been found. if this is turned off, the only fallback will be the default file
     * (e.g. "messages.properties" for basename "messages").
     */
    private boolean fallbackToSystemLocale = true;

    /**
     * Set whether to always apply the MessageFormat rules, parsing even messages without
     * arguments.
     */
    private boolean alwaysUseMessageFormat = false;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        if (StringUtils.hasText(this.basename)) {
            messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(
                    StringUtils.trimAllWhitespace(this.basename)));
        }
        if (this.encoding != null) {
            messageSource.setDefaultEncoding(this.encoding.name());
        }
        messageSource.setFallbackToSystemLocale(this.fallbackToSystemLocale);
        messageSource.setCacheSeconds(this.cacheSeconds);
        messageSource.setAlwaysUseMessageFormat(this.alwaysUseMessageFormat);
        return messageSource;
    }

    public String getBasename() {
        return this.basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public int getCacheSeconds() {
        return this.cacheSeconds;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }

    public boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    public boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }

    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    protected static class ResourceBundleCondition extends SpringBootCondition {

        private static ConcurrentReferenceHashMap<String, ConditionOutcome> cache = new ConcurrentReferenceHashMap<String, ConditionOutcome>();

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context,
                                                AnnotatedTypeMetadata metadata) {
            String basename = context.getEnvironment()
                    .getProperty("spring.messages.basename", "messages");
            ConditionOutcome outcome = cache.get(basename);
            if (outcome == null) {
                outcome = getMatchOutcomeForBasename(context, basename);
                cache.put(basename, outcome);
            }
            return outcome;
        }

        private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context,
                                                            String basename) {
            ConditionMessage.Builder message = ConditionMessage
                    .forCondition("ResourceBundle");
            for (String name : StringUtils.commaDelimitedListToStringArray(
                    StringUtils.trimAllWhitespace(basename))) {
                for (Resource resource : getResources(context.getClassLoader(), name)) {
                    if (resource.exists()) {
                        return ConditionOutcome
                                .match(message.found("bundle").items(resource));
                    }
                }
            }
            return ConditionOutcome.noMatch(
                    message.didNotFind("bundle with basename " + basename).atAll());
        }

        private Resource[] getResources(ClassLoader classLoader, String name) {
            String target = name.replace('.', '/');
            try {
                return new PathMatchingResourcePatternResolver(classLoader)
                        .getResources("classpath*:" + target + ".properties");
            }
            catch (Exception ex) {
                return NO_RESOURCES;
            }
        }

    }

}

