package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.ByteArrayResource;
import com.rocket.summer.framework.core.io.Resource;

import java.io.IOException;

/**
 * An extension of {@link com.rocket.summer.framework.core.io.ByteArrayResource}
 * that a {@link ResourceTransformer} can use to represent an original
 * resource preserving all other information except the content.
 *
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class TransformedResource extends ByteArrayResource {

    private final String filename;

    private final long lastModified;


    public TransformedResource(Resource original, byte[] transformedContent) {
        super(transformedContent);
        this.filename = original.getFilename();
        try {
            this.lastModified = original.lastModified();
        }
        catch (IOException ex) {
            // should never happen
            throw new IllegalArgumentException(ex);
        }
    }


    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long lastModified() throws IOException {
        return this.lastModified;
    }

}
