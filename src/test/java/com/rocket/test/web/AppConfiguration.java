package com.rocket.test.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Locale;

public class AppConfiguration {

    private Log logger = LogFactory.getLog(AppConfiguration.class);

    /**
     *
     */
    public AppConfiguration() {
        // TODO Auto-generated constructor stub
        logger.info("[Initialize application]");
        Locale.setDefault(Locale.US);
    }

}
