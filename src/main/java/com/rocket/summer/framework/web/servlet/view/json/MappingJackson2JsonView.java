package com.rocket.summer.framework.web.servlet.view.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.rocket.summer.framework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.rocket.summer.framework.http.converter.json.MappingJacksonValue;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Spring MVC {@link View} that renders JSON content by serializing the model for the current request
 * using <a href="https://wiki.fasterxml.com/JacksonHome">Jackson 2's</a> {@link ObjectMapper}.
 *
 * <p>By default, the entire contents of the model map (with the exception of framework-specific classes)
 * will be encoded as JSON. If the model contains only one key, you can have it extracted encoded as JSON
 * alone via  {@link #setExtractValueFromSingleKeyModel}.
 *
 * <p>The default constructor uses the default configuration provided by {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * @author Jeremy Grelle
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 3.1.2
 */
@SuppressWarnings("deprecation")
public class MappingJackson2JsonView extends AbstractJackson2View {

    /**
     * Default content type: "application/json".
     * Overridable through {@link #setContentType}.
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    /**
     * Default content type for JSONP: "application/javascript".
     * @deprecated Will be removed as of Spring Framework 5.1, use
     * <a href="https://docs.spring.io/spring/docs/4.3.x/spring-framework-reference/html/cors.html">CORS</a> instead.
     */
    @Deprecated
    public static final String DEFAULT_JSONP_CONTENT_TYPE = "application/javascript";

    /**
     * Pattern for validating jsonp callback parameter values.
     */
    private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");


    private String jsonPrefix;

    private Set<String> modelKeys;

    private boolean extractValueFromSingleKeyModel = false;

    private Set<String> jsonpParameterNames = new LinkedHashSet<String>();


    /**
     * Construct a new {@code MappingJackson2JsonView} using default configuration
     * provided by {@link Jackson2ObjectMapperBuilder} and setting the content type
     * to {@code application/json}.
     */
    public MappingJackson2JsonView() {
        super(Jackson2ObjectMapperBuilder.json().build(), DEFAULT_CONTENT_TYPE);
    }

    /**
     * Construct a new {@code MappingJackson2JsonView} using the provided
     * {@link ObjectMapper} and setting the content type to {@code application/json}.
     * @since 4.2.1
     */
    public MappingJackson2JsonView(ObjectMapper objectMapper) {
        super(objectMapper, DEFAULT_CONTENT_TYPE);
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
     * Indicates whether the JSON output by this view should be prefixed with <tt>")]}', "</tt>.
     * Default is {@code false}.
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking.
     * The prefix renders the string syntactically invalid as a script so that it cannot be hijacked.
     * This prefix should be stripped before parsing the string as JSON.
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? ")]}', " : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModelKey(String modelKey) {
        this.modelKeys = Collections.singleton(modelKey);
    }

    /**
     * Set the attributes in the model that should be rendered by this view.
     * When set, all other model attributes will be ignored.
     */
    public void setModelKeys(Set<String> modelKeys) {
        this.modelKeys = modelKeys;
    }

    /**
     * Return the attributes in the model that should be rendered by this view.
     */
    public final Set<String> getModelKeys() {
        return this.modelKeys;
    }

    /**
     * Set whether to serialize models containing a single attribute as a map or
     * whether to extract the single value from the model and serialize it directly.
     * <p>The effect of setting this flag is similar to using
     * {@code MappingJackson2HttpMessageConverter} with an {@code @ResponseBody}
     * request-handling method.
     * <p>Default is {@code false}.
     */
    public void setExtractValueFromSingleKeyModel(boolean extractValueFromSingleKeyModel) {
        this.extractValueFromSingleKeyModel = extractValueFromSingleKeyModel;
    }

    /**
     * Set JSONP request parameter names. Each time a request has one of those
     * parameters, the resulting JSON will be wrapped into a function named as
     * specified by the JSONP request parameter value.
     * <p>The parameter names configured by default are "jsonp" and "callback".
     * @since 4.1
     * @see <a href="https://en.wikipedia.org/wiki/JSONP">JSONP Wikipedia article</a>
     * @deprecated Will be removed as of Spring Framework 5.1, use
     * <a href="https://docs.spring.io/spring/docs/4.3.x/spring-framework-reference/html/cors.html">CORS</a> instead.
     */
    @Deprecated
    public void setJsonpParameterNames(Set<String> jsonpParameterNames) {
        this.jsonpParameterNames = jsonpParameterNames;
    }

    private String getJsonpParameterValue(HttpServletRequest request) {
        if (this.jsonpParameterNames != null) {
            for (String name : this.jsonpParameterNames) {
                String value = request.getParameter(name);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                if (!isValidJsonpQueryParam(value)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring invalid jsonp parameter value: " + value);
                    }
                    continue;
                }
                return value;
            }
        }
        return null;
    }

    /**
     * Validate the jsonp query parameter value. The default implementation
     * returns true if it consists of digits, letters, or "_" and ".".
     * Invalid parameter values are ignored.
     * @param value the query param value, never {@code null}
     * @since 4.1.8
     * @deprecated Will be removed as of Spring Framework 5.1, use
     * <a href="https://docs.spring.io/spring/docs/4.3.x/spring-framework-reference/html/cors.html">CORS</a> instead.
     */
    @Deprecated
    protected boolean isValidJsonpQueryParam(String value) {
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }

    /**
     * Filter out undesired attributes from the given model.
     * The return value can be either another {@link Map} or a single value object.
     * <p>The default implementation removes {@link BindingResult} instances and entries
     * not included in the {@link #setModelKeys modelKeys} property.
     * @param model the model, as passed on to {@link #renderMergedOutputModel}
     * @return the value to be rendered
     */
    @Override
    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> result = new HashMap<String, Object>(model.size());
        Set<String> modelKeys = (!CollectionUtils.isEmpty(this.modelKeys) ? this.modelKeys : model.keySet());
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!(entry.getValue() instanceof BindingResult) && modelKeys.contains(entry.getKey()) &&
                    !entry.getKey().equals(JsonView.class.getName()) &&
                    !entry.getKey().equals(FilterProvider.class.getName())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return (this.extractValueFromSingleKeyModel && result.size() == 1 ? result.values().iterator().next() : result);
    }

    @Override
    protected Object filterAndWrapModel(Map<String, Object> model, HttpServletRequest request) {
        Object value = super.filterAndWrapModel(model, request);
        String jsonpParameterValue = getJsonpParameterValue(request);
        if (jsonpParameterValue != null) {
            if (value instanceof MappingJacksonValue) {
                ((MappingJacksonValue) value).setJsonpFunction(jsonpParameterValue);
            }
            else {
                MappingJacksonValue container = new MappingJacksonValue(value);
                container.setJsonpFunction(jsonpParameterValue);
                value = container;
            }
        }
        return value;
    }

    @Override
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }

        String jsonpFunction = null;
        if (object instanceof MappingJacksonValue) {
            jsonpFunction = ((MappingJacksonValue) object).getJsonpFunction();
        }
        if (jsonpFunction != null) {
            generator.writeRaw("/**/");
            generator.writeRaw(jsonpFunction + "(");
        }
    }

    @Override
    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
        String jsonpFunction = null;
        if (object instanceof MappingJacksonValue) {
            jsonpFunction = ((MappingJacksonValue) object).getJsonpFunction();
        }
        if (jsonpFunction != null) {
            generator.writeRaw(");");
        }
    }

    @Override
    protected void setResponseContentType(HttpServletRequest request, HttpServletResponse response) {
        if (getJsonpParameterValue(request) != null) {
            response.setContentType(DEFAULT_JSONP_CONTENT_TYPE);
        }
        else {
            super.setResponseContentType(request, response);
        }
    }

}

