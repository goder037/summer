package com.rocket.summer.framework.http.converter.json;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link HttpInputMessage} that can eventually stores a Jackson view that will be used
 * to deserialize the message.
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public class MappingJacksonInputMessage implements HttpInputMessage {

    private final InputStream body;

    private final HttpHeaders headers;

    private Class<?> deserializationView;


    public MappingJacksonInputMessage(InputStream body, HttpHeaders headers) {
        this.body = body;
        this.headers = headers;
    }

    public MappingJacksonInputMessage(InputStream body, HttpHeaders headers, Class<?> deserializationView) {
        this(body, headers);
        this.deserializationView = deserializationView;
    }


    @Override
    public InputStream getBody() throws IOException {
        return this.body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public void setDeserializationView(Class<?> deserializationView) {
        this.deserializationView = deserializationView;
    }

    public Class<?> getDeserializationView() {
        return this.deserializationView;
    }

}

