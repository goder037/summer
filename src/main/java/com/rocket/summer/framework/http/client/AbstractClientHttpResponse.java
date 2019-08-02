package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.http.HttpStatus;
import com.rocket.summer.framework.web.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Abstract base for {@link ClientHttpResponse}.
 *
 * @author Arjen Poutsma
 * @since 3.1.1
 */
public abstract class AbstractClientHttpResponse implements ClientHttpResponse {

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(getRawStatusCode());
    }

}
