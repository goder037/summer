package com.rocket.summer.framework.boot.autoconfigure.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.rocket.summer.framework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Callback interface that can be implemented by beans wishing to further customize the
 * {@link ObjectMapper} via {@link Jackson2ObjectMapperBuilder} retaining its default
 * auto-configuration.
 *
 * @author Grzegorz Poznachowski
 * @since 1.4.0
 */
public interface Jackson2ObjectMapperBuilderCustomizer {

    /**
     * Customize the JacksonObjectMapperBuilder.
     * @param jacksonObjectMapperBuilder the JacksonObjectMapperBuilder to customize
     */
    void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder);

}

