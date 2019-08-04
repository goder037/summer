package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.DigestUtils;
import com.rocket.summer.framework.util.FileCopyUtils;

import java.io.IOException;

/**
 * A {@code VersionStrategy} that calculates an Hex MD5 hashes from the content
 * of the resource and appends it to the file name, e.g.
 * {@code "styles/main-e36d2e05253c6c7085a91522ce43a0b4.css"}.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see VersionResourceResolver
 */
public class ContentVersionStrategy extends AbstractVersionStrategy {

    public ContentVersionStrategy() {
        super(new FileNameVersionPathStrategy());
    }

    @Override
    public String getResourceVersion(Resource resource) {
        try {
            byte[] content = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return DigestUtils.md5DigestAsHex(content);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to calculate hash for " + resource, ex);
        }
    }

}

