package com.rocket.summer.framework.http.converter.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.json.AbstractJackson2HttpMessageConverter;
import com.rocket.summer.framework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.rocket.summer.framework.util.Assert;

/**
 * Implementation of {@link com.rocket.summer.framework.http.converter.HttpMessageConverter HttpMessageConverter}
 * that can read and write XML using <a href="https://github.com/FasterXML/jackson-dataformat-xml">
 * Jackson 2.x extension component for reading and writing XML encoded data</a>.
 *
 * <p>By default, this converter supports {@code application/xml}, {@code text/xml}, and
 * {@code application/*+xml} with {@code UTF-8} character set. This can be overridden by
 * setting the {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * <p>The default constructor uses the default configuration provided by {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * @author Sebastien Deleuze
 * @since 4.1
 */
public class MappingJackson2XmlHttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    /**
     * Construct a new {@code MappingJackson2XmlHttpMessageConverter} using default configuration
     * provided by {@code Jackson2ObjectMapperBuilder}.
     */
    public MappingJackson2XmlHttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.xml().build());
    }

    /**
     * Construct a new {@code MappingJackson2XmlHttpMessageConverter} with a custom {@link ObjectMapper}
     * (must be a {@link XmlMapper} instance).
     * You can use {@link Jackson2ObjectMapperBuilder} to build it easily.
     * @see Jackson2ObjectMapperBuilder#xml()
     */
    public MappingJackson2XmlHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType("application", "xml"),
                new MediaType("text", "xml"),
                new MediaType("application", "*+xml"));
        Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
    }


    /**
     * {@inheritDoc}
     * The {@code ObjectMapper} parameter must be a {@link XmlMapper} instance.
     */
    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
        super.setObjectMapper(objectMapper);
    }

}

