package com.rocket.summer.framework.http.converter.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.HttpOutputMessage;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.converter.AbstractHttpMessageConverter;
import com.rocket.summer.framework.http.converter.HttpMessageNotReadableException;
import com.rocket.summer.framework.http.converter.HttpMessageNotWritableException;
import com.rocket.summer.framework.util.Assert;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Implementation of {@link com.rocket.summer.framework.http.converter.HttpMessageConverter} that can read and
 * write JSON using <a href="https://wiki.fasterxml.com/JacksonHome">Jackson 2.x's</a> {@link ObjectMapper}.
 *
 * <p>This converter can be used to bind to typed beans, or untyped {@code HashMap} instances.
 *
 * <p>By default, this converter supports {@code application/json} and {@code application/*+json}
 * with {@code UTF-8} character set. This can be overridden by setting the
 * {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * <p>The default constructor uses the default configuration provided by {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * @author Arjen Poutsma
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 3.1.2
 */
public class MappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    private String jsonPrefix;


    /**
     * Construct a new {@link MappingJackson2HttpMessageConverter} using default configuration
     * provided by {@link Jackson2ObjectMapperBuilder}.
     */
    public MappingJackson2HttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    /**
     * Construct a new {@link MappingJackson2HttpMessageConverter} with a custom {@link ObjectMapper}.
     * You can use {@link Jackson2ObjectMapperBuilder} to build it easily.
     * @see Jackson2ObjectMapperBuilder#json()
     */
    public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    /**
     * Specify a custom prefix to use for this view's JSON output.
     * Default is none.
     * @see #setPrefixJson
     */
    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    /**
     * Indicate whether the JSON output by this view should be prefixed with ")]}', ". Default is false.
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking.
     * The prefix renders the string syntactically invalid as a script so that it cannot be hijacked.
     * This prefix should be stripped before parsing the string as JSON.
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? ")]}', " : null);
    }


    @Override
    @SuppressWarnings("deprecation")
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
        String jsonpFunction =
                (object instanceof MappingJacksonValue ? ((MappingJacksonValue) object).getJsonpFunction() : null);
        if (jsonpFunction != null) {
            generator.writeRaw("/**/");
            generator.writeRaw(jsonpFunction + "(");
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
        String jsonpFunction =
                (object instanceof MappingJacksonValue ? ((MappingJacksonValue) object).getJsonpFunction() : null);
        if (jsonpFunction != null) {
            generator.writeRaw(");");
        }
    }

}
