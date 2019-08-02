package com.rocket.summer.framework.web.accept;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Extends {@code PathExtensionContentNegotiationStrategy} that also uses
 * {@link ServletContext#getMimeType(String)} to resolve file extensions.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ServletPathExtensionContentNegotiationStrategy extends PathExtensionContentNegotiationStrategy {

    private final ServletContext servletContext;


    /**
     * Create an instance without any mappings to start with. Mappings may be
     * added later when extensions are resolved through
     * {@link ServletContext#getMimeType(String)} or via JAF.
     */
    public ServletPathExtensionContentNegotiationStrategy(ServletContext context) {
        this(context, null);
    }

    /**
     * Create an instance with the given extension-to-MediaType lookup.
     */
    public ServletPathExtensionContentNegotiationStrategy(
            ServletContext servletContext, Map<String, MediaType> mediaTypes) {

        super(mediaTypes);
        Assert.notNull(servletContext, "ServletContext is required");
        this.servletContext = servletContext;
    }


    /**
     * Resolve file extension via {@link ServletContext#getMimeType(String)}
     * and also delegate to base class for a potential JAF lookup.
     */
    @Override
    protected MediaType handleNoMatch(NativeWebRequest webRequest, String extension)
            throws HttpMediaTypeNotAcceptableException {

        MediaType mediaType = null;
        if (this.servletContext != null) {
            String mimeType = this.servletContext.getMimeType("file." + extension);
            if (StringUtils.hasText(mimeType)) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        }
        if (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
            MediaType superMediaType = super.handleNoMatch(webRequest, extension);
            if (superMediaType != null) {
                mediaType = superMediaType;
            }
        }
        return mediaType;
    }

    /**
     * Extends the base class
     * {@link PathExtensionContentNegotiationStrategy#getMediaTypeForResource}
     * with the ability to also look up through the ServletContext.
     * @param resource the resource to look up
     * @return the MediaType for the extension, or {@code null} if none found
     * @since 4.3
     */
    public MediaType getMediaTypeForResource(Resource resource) {
        MediaType mediaType = null;
        if (this.servletContext != null) {
            String mimeType = this.servletContext.getMimeType(resource.getFilename());
            if (StringUtils.hasText(mimeType)) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        }
        if (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
            MediaType superMediaType = super.getMediaTypeForResource(resource);
            if (superMediaType != null) {
                mediaType = superMediaType;
            }
        }
        return mediaType;
    }

}

