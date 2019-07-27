package com.rocket.summer.framework.http.server;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpStatus;
import com.rocket.summer.framework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * {@link ServerHttpResponse} implementation that is based on a {@link HttpServletResponse}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class ServletServerHttpResponse implements ServerHttpResponse {

    private final HttpServletResponse servletResponse;

    private final HttpHeaders headers = new HttpHeaders();

    private boolean headersWritten = false;


    /**
     * Construct a new instance of the ServletServerHttpResponse based on the given {@link HttpServletResponse}.
     * @param servletResponse the servlet response
     */
    public ServletServerHttpResponse(HttpServletResponse servletResponse) {
        Assert.notNull(servletResponse, "'servletResponse' must not be null");
        this.servletResponse = servletResponse;
    }


    /**
     * Return the {@code HttpServletResponse} this object is based on.
     */
    public HttpServletResponse getServletResponse() {
        return this.servletResponse;
    }

    public void setStatusCode(HttpStatus status) {
        this.servletResponse.setStatus(status.value());
    }

    public HttpHeaders getHeaders() {
        return (this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
    }

    public OutputStream getBody() throws IOException {
        writeHeaders();
        return this.servletResponse.getOutputStream();
    }

    public void close() {
        writeHeaders();
    }

    private void writeHeaders() {
        if (!this.headersWritten) {
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                String headerName = entry.getKey();
                for (String headerValue : entry.getValue()) {
                    this.servletResponse.addHeader(headerName, headerValue);
                }
            }
            // HttpServletResponse exposes some headers as properties: we should include those if not already present
            if (this.servletResponse.getContentType() == null && this.headers.getContentType() != null) {
                this.servletResponse.setContentType(this.headers.getContentType().toString());
            }
            if (this.servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null &&
                    this.headers.getContentType().getCharSet() != null) {
                this.servletResponse.setCharacterEncoding(this.headers.getContentType().getCharSet().name());
            }
            this.headersWritten = true;
        }
    }

}
