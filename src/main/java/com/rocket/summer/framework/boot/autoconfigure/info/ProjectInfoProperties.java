package com.rocket.summer.framework.boot.autoconfigure.info;

import com.rocket.summer.framework.beans.factory.annotation.Autowired;
import com.rocket.summer.framework.beans.factory.annotation.Value;
import com.rocket.summer.framework.boot.context.properties.ConfigurationProperties;
import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.core.io.Resource;

/**
 * Configuration properties for project information.
 *
 * @author Stephane Nicoll
 * @since 1.4.0
 */
@ConfigurationProperties(prefix = "spring.info")
public class ProjectInfoProperties {

    private final Build build = new Build();

    private final Git git = new Git();

    public Build getBuild() {
        return this.build;
    }

    public Git getGit() {
        return this.git;
    }

    /**
     * Make sure that the "spring.git.properties" legacy key is used by default.
     * @param defaultGitLocation the default git location to use
     */
    @Autowired
    void setDefaultGitLocation(
            @Value("${spring.git.properties:classpath:git.properties}") Resource defaultGitLocation) {
        getGit().setLocation(defaultGitLocation);
    }

    /**
     * Build specific info properties.
     */
    public static class Build {

        /**
         * Location of the generated build-info.properties file.
         */
        private Resource location = new ClassPathResource(
                "META-INF/build-info.properties");

        public Resource getLocation() {
            return this.location;
        }

        public void setLocation(Resource location) {
            this.location = location;
        }

    }

    /**
     * Git specific info properties.
     */
    public static class Git {

        /**
         * Location of the generated git.properties file.
         */
        private Resource location;

        public Resource getLocation() {
            return this.location;
        }

        public void setLocation(Resource location) {
            this.location = location;
        }

    }

}

