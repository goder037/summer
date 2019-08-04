package com.rocket.summer.framework.http.server;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpMethod;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * {@link ServerHttpRequest} implementation that is based on a {@link HttpServletRequest}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class ServletServerHttpRequest implements ServerHttpRequest {

    protected static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    protected static final String FORM_CHARSET = "UTF-8";

    private static final String METHOD_POST = "POST";

    private final HttpServletRequest servletRequest;

    private HttpHeaders headers;


    /**
     * Construct a new instance of the ServletServerHttpRequest based on the given {@link HttpServletRequest}.
     * @param servletRequest the servlet request
     */
    public ServletServerHttpRequest(HttpServletRequest servletRequest) {
        Assert.notNull(servletRequest, "'servletRequest' must not be null");
        this.servletRequest = servletRequest;
    }


    /**
     * Returns the {@code HttpServletRequest} this object is based on.
     */
    public HttpServletRequest getServletRequest() {
        return this.servletRequest;
    }

    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.servletRequest.getMethod());
    }

    public URI getURI() {
        try {
            return new URI(this.servletRequest.getScheme(), null, this.servletRequest.getServerName(),
                    this.servletRequest.getServerPort(), this.servletRequest.getRequestURI(),
                    this.servletRequest.getQueryString(), null);
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpServletRequest URI: " + ex.getMessage(), ex);
        }
    }

    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            for (Enumeration<?> headerNames = this.servletRequest.getHeaderNames(); headerNames.hasMoreElements();) {
                String headerName = (String) headerNames.nextElement();
                for (Enumeration<?> headerValues = this.servletRequest.getHeaders(headerName);
                     headerValues.hasMoreElements();) {
                    String headerValue = (String) headerValues.nextElement();
                    this.headers.add(headerName, headerValue);
                }
            }
            // HttpServletRequest exposes some headers as properties: we should include those if not already present
            if (this.headers.getContentType() == null && this.servletRequest.getContentType() != null) {
                MediaType contentType = MediaType.parseMediaType(this.servletRequest.getContentType());
                this.headers.setContentType(contentType);
            }
            if (this.headers.getContentType() != null && this.headers.getContentType().getCharset() == null &&
                    this.servletRequest.getCharacterEncoding() != null) {
                MediaType oldContentType = this.headers.getContentType();
                Charset charSet = Charset.forName(this.servletRequest.getCharacterEncoding());
                Map<String, String> params = new HashMap<String, String>(oldContentType.getParameters());
                params.put("charset", charSet.toString());
                MediaType newContentType = new MediaType(oldContentType.getType(), oldContentType.getSubtype(), params);
                this.headers.setContentType(newContentType);
            }
            if (this.headers.getContentLength() == -1 && this.servletRequest.getContentLength() != -1) {
                this.headers.setContentLength(this.servletRequest.getContentLength());
            }
        }
        return this.headers;
    }

    public InputStream getBody() throws IOException {
        if (isFormPost(this.servletRequest)) {
            return getBodyFromServletRequestParameters(this.servletRequest);
        }
        else {
            return this.servletRequest.getInputStream();
        }
    }

    private boolean isFormPost(HttpServletRequest request) {
        return (request.getContentType() != null && request.getContentType().contains(FORM_CONTENT_TYPE) &&
                METHOD_POST.equalsIgnoreCase(request.getMethod()));
    }

    /**
     * Use {@link javax.servlet.ServletRequest#getParameterMap()} to reconstruct the
     * body of a form 'POST' providing a predictable outcome as opposed to reading
     * from the body, which can fail if any other code has used ServletRequest
     * to access a parameter thus causing the input stream to be "consumed".
     */
    private InputStream getBodyFromServletRequestParameters(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bos, FORM_CHARSET);

        Map<String, String[]> form = request.getParameterMap();
        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
            String name = nameIterator.next();
            List<String> values = Arrays.asList(form.get(name));
            for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();) {
                String value = valueIterator.next();
                writer.write(URLEncoder.encode(name, FORM_CHARSET));
                if (value != null) {
                    writer.write('=');
                    writer.write(URLEncoder.encode(value, FORM_CHARSET));
                    if (valueIterator.hasNext()) {
                        writer.write('&');
                    }
                }
            }
            if (nameIterator.hasNext()) {
                writer.append('&');
            }
        }
        writer.flush();

        return new ByteArrayInputStream(bos.toByteArray());
    }

}