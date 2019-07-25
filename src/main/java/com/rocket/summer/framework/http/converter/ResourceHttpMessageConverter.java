package com.rocket.summer.framework.http.converter;

import com.rocket.summer.framework.core.io.ByteArrayResource;
import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.http.HttpInputMessage;
import com.rocket.summer.framework.http.HttpOutputMessage;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.FileCopyUtils;
import com.rocket.summer.framework.util.StringUtils;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link HttpMessageConverter} that can read and write {@link Resource Resources}.
 *
 * <p>By default, this converter can read all media types. The Java Activation Framework (JAF) -
 * if available - is used to determine the {@code Content-Type} of written resources.
 * If JAF is not available, {@code application/octet-stream} is used.
 *
 * @author Arjen Poutsma
 * @since 3.0.2
 */
public class ResourceHttpMessageConverter extends AbstractHttpMessageConverter<Resource> {

    private static final boolean jafPresent =
            ClassUtils.isPresent("javax.activation.FileTypeMap", ResourceHttpMessageConverter.class.getClassLoader());


    public ResourceHttpMessageConverter() {
        super(MediaType.ALL);
    }


    @Override
    protected boolean supports(Class<?> clazz) {
        return Resource.class.isAssignableFrom(clazz);
    }

    @Override
    protected Resource readInternal(Class<? extends Resource> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        byte[] body = FileCopyUtils.copyToByteArray(inputMessage.getBody());
        return new ByteArrayResource(body);
    }

    @Override
    protected MediaType getDefaultContentType(Resource resource) {
        if (jafPresent) {
            return ActivationMediaTypeFactory.getMediaType(resource);
        }
        else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    @Override
    protected Long getContentLength(Resource resource, MediaType contentType) throws IOException {
        return resource.contentLength();
    }

    @Override
    protected void writeInternal(Resource resource, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        FileCopyUtils.copy(resource.getInputStream(), outputMessage.getBody());
        outputMessage.getBody().flush();
    }


    /**
     * Inner class to avoid hard-coded JAF dependency.
     */
    private static class ActivationMediaTypeFactory {

        private static final FileTypeMap fileTypeMap;

        static {
            fileTypeMap = loadFileTypeMapFromContextSupportModule();
        }

        private static FileTypeMap loadFileTypeMapFromContextSupportModule() {
            // see if we can find the extended mime.types from the context-support module
            Resource mappingLocation = new ClassPathResource("org/springframework/mail/javamail/mime.types");
            if (mappingLocation.exists()) {
                InputStream inputStream = null;
                try {
                    inputStream = mappingLocation.getInputStream();
                    return new MimetypesFileTypeMap(inputStream);
                }
                catch (IOException ex) {
                    // ignore
                }
                finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException ex) {
                            // ignore
                        }
                    }
                }
            }
            return FileTypeMap.getDefaultFileTypeMap();
        }

        public static MediaType getMediaType(Resource resource) {
            String mediaType = fileTypeMap.getContentType(resource.getFilename());
            return (StringUtils.hasText(mediaType) ? MediaType.parseMediaType(mediaType) : null);
        }
    }

}

