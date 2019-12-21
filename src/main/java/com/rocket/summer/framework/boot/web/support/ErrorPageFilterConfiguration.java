package com.rocket.summer.framework.boot.web.support;

import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;

/**
 * Configuration for {@link ErrorPageFilter}.
 *
 * @author Andy Wilkinson
 */
@Configuration
class ErrorPageFilterConfiguration {

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

}

