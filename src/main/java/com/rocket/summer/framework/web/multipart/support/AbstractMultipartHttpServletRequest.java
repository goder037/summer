package com.rocket.summer.framework.web.multipart.support;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpMethod;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.web.multipart.MultipartFile;
import com.rocket.summer.framework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * Abstract base implementation of the MultipartHttpServletRequest interface.
 * Provides management of pre-generated MultipartFile instances.
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @since 06.10.2003
 */
public abstract class AbstractMultipartHttpServletRequest extends HttpServletRequestWrapper
        implements MultipartHttpServletRequest {

    private MultiValueMap<String, MultipartFile> multipartFiles;


    /**
     * Wrap the given HttpServletRequest in a MultipartHttpServletRequest.
     * @param request the request to wrap
     */
    protected AbstractMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }


    @Override
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    public HttpMethod getRequestMethod() {
        return HttpMethod.valueOf(getRequest().getMethod());
    }

    public HttpHeaders getRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, Collections.list(getHeaders(headerName)));
        }
        return headers;
    }

    public Iterator<String> getFileNames() {
        return getMultipartFiles().keySet().iterator();
    }

    public MultipartFile getFile(String name) {
        return getMultipartFiles().getFirst(name);
    }

    public List<MultipartFile> getFiles(String name) {
        List<MultipartFile> multipartFiles = getMultipartFiles().get(name);
        if (multipartFiles != null) {
            return multipartFiles;
        }
        else {
            return Collections.emptyList();
        }
    }

    public Map<String, MultipartFile> getFileMap() {
        return getMultipartFiles().toSingleValueMap();
    }

    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        return getMultipartFiles();
    }


    /**
     * Set a Map with parameter names as keys and list of MultipartFile objects as values.
     * To be invoked by subclasses on initialization.
     */
    protected final void setMultipartFiles(MultiValueMap<String, MultipartFile> multipartFiles) {
        this.multipartFiles =
                new LinkedMultiValueMap<String, MultipartFile>(Collections.unmodifiableMap(multipartFiles));
    }

    /**
     * Obtain the MultipartFile Map for retrieval,
     * lazily initializing it if necessary.
     * @see #initializeMultipart()
     */
    protected MultiValueMap<String, MultipartFile> getMultipartFiles() {
        if (this.multipartFiles == null) {
            initializeMultipart();
        }
        return this.multipartFiles;
    }

    /**
     * Lazily initialize the multipart request, if possible.
     * Only called if not already eagerly initialized.
     */
    protected void initializeMultipart() {
        throw new IllegalStateException("Multipart request not initialized");
    }

}

