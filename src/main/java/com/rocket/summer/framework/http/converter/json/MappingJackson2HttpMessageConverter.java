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
 * Implementation of {@link com.rocket.summer.framework.http.converter.HttpMessageConverter HttpMessageConverter}
 * that can read and write JSON using <a href="http://jackson.codehaus.org/">Jackson 2's</a> {@link ObjectMapper}.
 *
 * <p>This converter can be used to bind to typed beans, or untyped {@link java.util.HashMap HashMap} instances.
 *
 * <p>By default, this converter supports {@code application/json}. This can be overridden by setting the
 * {@link #setSupportedMediaTypes(List) supportedMediaTypes} property.
 *
 * @author Arjen Poutsma
 * @author Keith Donald
 * @since 3.1.2
 * @see com.rocket.summer.framework.web.servlet.view.json.MappingJackson2JsonView
 */
public class MappingJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


    private ObjectMapper objectMapper = new ObjectMapper();

    private boolean prefixJson = false;


    /**
     * Construct a new {@code BindingJacksonHttpMessageConverter}.
     */
    public MappingJackson2HttpMessageConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET));
    }

    /**
     * Set the {@code ObjectMapper} for this view. If not set, a default
     * {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
     * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further control of the JSON
     * serialization process. For example, an extended {@link com.fasterxml.jackson.databind.ser.SerializerFactory}
     * can be configured that provides custom serializers for specific types. The other option for refining
     * the serialization process is to use Jackson's provided annotations on the types to be serialized,
     * in which case a custom-configured ObjectMapper is unnecessary.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
    }

    /**
     * Return the underlying {@code ObjectMapper} for this view.
     */
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    /**
     * Indicate whether the JSON output by this view should be prefixed with "{} &&". Default is false.
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking.
     * The prefix renders the string syntactically invalid as a script so that it cannot be hijacked.
     * This prefix does not affect the evaluation of JSON, but if JSON validation is performed on the
     * string, the prefix would need to be ignored.
     */
    public void setPrefixJson(boolean prefixJson) {
        this.prefixJson = prefixJson;
    }


    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        JavaType javaType = getJavaType(clazz);
        return (this.objectMapper.canDeserialize(javaType) && canRead(mediaType));
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return (this.objectMapper.canSerialize(clazz) && canWrite(mediaType));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(clazz);
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        JsonGenerator jsonGenerator =
                this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
        try {
            if (this.prefixJson) {
                jsonGenerator.writeRaw("{} && ");
            }
            this.objectMapper.writeValue(jsonGenerator, object);
        }
        catch (IOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }


    /**
     * Return the Jackson {@link JavaType} for the specified class.
     * <p>The default implementation returns {@link ObjectMapper#constructType(java.lang.reflect.Type)},
     * but this can be overridden in subclasses, to allow for custom generic collection handling.
     * For instance:
     * <pre class="code">
     * protected JavaType getJavaType(Class&lt;?&gt; clazz) {
     *   if (List.class.isAssignableFrom(clazz)) {
     *     return objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, MyBean.class);
     *   } else {
     *     return super.getJavaType(clazz);
     *   }
     * }
     * </pre>
     * @param clazz the class to return the java type for
     * @return the java type
     */
    protected JavaType getJavaType(Class<?> clazz) {
        return objectMapper.constructType(clazz);
    }

    /**
     * Determine the JSON encoding to use for the given content type.
     * @param contentType the media type as requested by the caller
     * @return the JSON encoding to use (never <code>null</code>)
     */
    protected JsonEncoding getJsonEncoding(MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            Charset charset = contentType.getCharSet();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }

}