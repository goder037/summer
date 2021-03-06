package com.rocket.summer.framework.web.multipart.support;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.util.FileCopyUtils;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.web.multipart.MultipartException;
import com.rocket.summer.framework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Spring MultipartHttpServletRequest adapter, wrapping a Servlet 3.0 HttpServletRequest
 * and its Part objects. Parameters get exposed through the native request's getParameter
 * methods - without any custom processing on our side.
 *
 * @author Juergen Hoeller
 * @since 3.1
 */
public class StandardMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest {

    private static final String CONTENT_DISPOSITION = "content-disposition";

    private static final String FILENAME_KEY = "filename=";


    /**
     * Create a new StandardMultipartHttpServletRequest wrapper for the given request.
     * @param request the servlet request to wrap
     * @throws MultipartException if parsing failed
     */
    public StandardMultipartHttpServletRequest(HttpServletRequest request) throws MultipartException {
        super(request);
        try {
            Collection<Part> parts = request.getParts();
            MultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<String, MultipartFile>(parts.size());
            for (Part part : parts) {
                String filename = extractFilename(part.getHeader(CONTENT_DISPOSITION));
                if (filename != null) {
                    files.add(part.getName(), new StandardMultipartFile(part, filename));
                }
            }
            setMultipartFiles(files);
        }
        catch (Exception ex) {
            throw new MultipartException("Could not parse multipart servlet request", ex);
        }
    }

    private String extractFilename(String contentDisposition) {
        if (contentDisposition == null) {
            return null;
        }
        // TODO: can only handle the typical case at the moment
        int startIndex = contentDisposition.indexOf(FILENAME_KEY);
        if (startIndex == -1) {
            return null;
        }
        String filename = contentDisposition.substring(startIndex + FILENAME_KEY.length());
        if (filename.startsWith("\"")) {
            int endIndex = filename.indexOf("\"", 1);
            if (endIndex != -1) {
                return filename.substring(1, endIndex);
            }
        }
        else {
            int endIndex = filename.indexOf(";");
            if (endIndex != -1) {
                return filename.substring(0, endIndex);
            }
        }
        return filename;
    }


    public String getMultipartContentType(String paramOrFileName) {
        try {
            Part part = getPart(paramOrFileName);
            return (part != null ? part.getContentType() : null);
        }
        catch (Exception ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }

    public HttpHeaders getMultipartHeaders(String paramOrFileName) {
        try {
            Part part = getPart(paramOrFileName);
            if (part != null) {
                HttpHeaders headers = new HttpHeaders();
                for (String headerName : part.getHeaderNames()) {
                    headers.put(headerName, new ArrayList<String>(part.getHeaders(headerName)));
                }
                return headers;
            }
            else {
                return null;
            }
        }
        catch (Exception ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }


    /**
     * Spring MultipartFile adapter, wrapping a Servlet 3.0 Part object.
     */
    private static class StandardMultipartFile implements MultipartFile {

        private final Part part;

        private final String filename;

        public StandardMultipartFile(Part part, String filename) {
            this.part = part;
            this.filename = filename;
        }

        public String getName() {
            return this.part.getName();
        }

        public String getOriginalFilename() {
            return this.filename;
        }

        public String getContentType() {
            return this.part.getContentType();
        }

        public boolean isEmpty() {
            return (this.part.getSize() == 0);
        }

        public long getSize() {
            return this.part.getSize();
        }

        public byte[] getBytes() throws IOException {
            return FileCopyUtils.copyToByteArray(this.part.getInputStream());
        }

        public InputStream getInputStream() throws IOException {
            return this.part.getInputStream();
        }

        public void transferTo(File dest) throws IOException, IllegalStateException {
            this.part.write(dest.getPath());
        }
    }

}