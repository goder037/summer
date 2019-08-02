package com.rocket.summer.framework.web.accept;

import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * A {@code ContentNegotiationStrategy} that resolves a query parameter to a key
 * to be used to look up a media type. The default parameter name is {@code format}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ParameterContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {

    private static final Log logger = LogFactory.getLog(ParameterContentNegotiationStrategy.class);

    private String parameterName = "format";


    /**
     * Create an instance with the given map of file extensions and media types.
     */
    public ParameterContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
    }


    /**
     * Set the name of the parameter to use to determine requested media types.
     * <p>By default this is set to {@code "format"}.
     */
    public void setParameterName(String parameterName) {
        Assert.notNull(parameterName, "'parameterName' is required");
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return this.parameterName;
    }


    @Override
    protected String getMediaTypeKey(NativeWebRequest request) {
        return request.getParameter(getParameterName());
    }

    @Override
    protected void handleMatch(String mediaTypeKey, MediaType mediaType) {
        if (logger.isDebugEnabled()) {
            logger.debug("Requested media type: '" + mediaType + "' based on '" +
                    getParameterName() + "'='" + mediaTypeKey + "'");
        }
    }

    @Override
    protected MediaType handleNoMatch(NativeWebRequest request, String key)
            throws HttpMediaTypeNotAcceptableException {

        throw new HttpMediaTypeNotAcceptableException(getAllMediaTypes());
    }

}

