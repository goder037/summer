package com.rocket.summer.framework.boot.autoconfigure.web;

import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.beans.factory.ObjectProvider;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureAfter;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.converter.StringHttpMessageConverter;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link HttpMessageConverter}s.
 *
 * @author Dave Syer
 * @author Christian Dupuis
 * @author Piotr Maj
 * @author Oliver Gierke
 * @author David Liu
 * @author Andy Wilkinson
 * @author Sebastien Deleuze
 * @author Stephane Nicoll
 */
@Configuration
@ConditionalOnClass(HttpMessageConverter.class)
@AutoConfigureAfter({ JacksonAutoConfiguration.class })
@Import({ JacksonHttpMessageConvertersConfiguration.class })
public class HttpMessageConvertersAutoConfiguration {

    static final String PREFERRED_MAPPER_PROPERTY = "spring.http.converters.preferred-json-mapper";

    private final List<HttpMessageConverter<?>> converters;

    public HttpMessageConvertersAutoConfiguration(
            ObjectProvider<List<HttpMessageConverter<?>>> convertersProvider) {
        this.converters = convertersProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters() {
        return new HttpMessageConverters((this.converters != null) ? this.converters
                : Collections.<HttpMessageConverter<?>>emptyList());
    }

    @Configuration
    @ConditionalOnClass(StringHttpMessageConverter.class)
    @EnableConfigurationProperties(HttpEncodingProperties.class)
    protected static class StringHttpMessageConverterConfiguration {

        private final HttpEncodingProperties encodingProperties;

        protected StringHttpMessageConverterConfiguration(
                HttpEncodingProperties encodingProperties) {
            this.encodingProperties = encodingProperties;
        }

        @Bean
        @ConditionalOnMissingBean
        public StringHttpMessageConverter stringHttpMessageConverter() {
            StringHttpMessageConverter converter = new StringHttpMessageConverter(
                    this.encodingProperties.getCharset());
            converter.setWriteAcceptCharset(false);
            return converter;
        }

    }

}

