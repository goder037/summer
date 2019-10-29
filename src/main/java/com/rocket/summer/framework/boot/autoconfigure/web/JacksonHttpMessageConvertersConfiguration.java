package com.rocket.summer.framework.boot.autoconfigure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.rocket.summer.framework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

/**
 * Configuration for HTTP message converters that use Jackson.
 *
 * @author Andy Wilkinson
 * @since 1.2.2
 */
@Configuration
class JacksonHttpMessageConvertersConfiguration {

    @Configuration
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnBean(ObjectMapper.class)
    @ConditionalOnProperty(
            name = HttpMessageConvertersAutoConfiguration.PREFERRED_MAPPER_PROPERTY,
            havingValue = "jackson", matchIfMissing = true)
    protected static class MappingJackson2HttpMessageConverterConfiguration {

        @Bean
        @ConditionalOnMissingBean(value = MappingJackson2HttpMessageConverter.class,
                ignoredType = {
                        "com.rocket.summer.framework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter",
                        "com.rocket.summer.framework.data.rest.webmvc.alps.AlpsJsonHttpMessageConverter" })
        public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
                ObjectMapper objectMapper) {
            return new MappingJackson2HttpMessageConverter(objectMapper);
        }

    }

    @Configuration
    @ConditionalOnClass(XmlMapper.class)
    @ConditionalOnBean(Jackson2ObjectMapperBuilder.class)
    protected static class MappingJackson2XmlHttpMessageConverterConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter(
                Jackson2ObjectMapperBuilder builder) {
            return new MappingJackson2XmlHttpMessageConverter(
                    builder.createXmlMapper(true).build());
        }

    }

}

