package com.rocket.summer.framework.boot;

/**
 * Class that exposes the Spring Boot version. Fetches the "Implementation-Version"
 * manifest attribute from the jar file.
 * <p>
 * Note that some ClassLoaders do not expose the package metadata, hence this class might
 * not be able to determine the Spring Boot version in all environments. Consider using a
 * reflection-based check instead: For example, checking for the presence of a specific
 * Spring Boot method that you intend to call.
 *
 * @author Drummond Dawson
 * @since 1.3.0
 */
public final class SpringBootVersion {

    private SpringBootVersion() {
    }

    /**
     * Return the full version string of the present Spring Boot codebase, or {@code null}
     * if it cannot be determined.
     * @return the version of Spring Boot or {@code null}
     * @see Package#getImplementationVersion()
     */
    public static String getVersion() {
        Package pkg = SpringApplication.class.getPackage();
        return (pkg != null) ? pkg.getImplementationVersion() : null;
    }

}
