package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.boot.context.embedded.*;
import com.rocket.summer.framework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import com.rocket.summer.framework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import com.rocket.summer.framework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import com.rocket.summer.framework.boot.context.properties.ConfigurationProperties;
import com.rocket.summer.framework.boot.context.properties.DeprecatedConfigurationProperty;
import com.rocket.summer.framework.boot.context.properties.NestedConfigurationProperty;
import com.rocket.summer.framework.boot.web.servlet.ServletContextInitializer;
import com.rocket.summer.framework.context.EnvironmentAware;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.catalina.valves.RemoteIpValve;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import java.io.File;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.*;

/**
 * {@link ConfigurationProperties} for a web server (e.g. port and path settings). Will be
 * used to customize an {@link EmbeddedServletContainerFactory} when an
 * {@link EmbeddedServletContainerCustomizerBeanPostProcessor} is active.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Ivan Sopov
 * @author Marcos Barbero
 * @author Eddú Meléndez
 * @author Quinten De Swaef
 * @author Venil Noronha
 * @author Aurélien Leboulanger
 */
@ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
public class ServerProperties
        implements EmbeddedServletContainerCustomizer, EnvironmentAware, Ordered {

    /**
     * Server HTTP port.
     */
    private Integer port;

    /**
     * Network address to which the server should bind to.
     */
    private InetAddress address;

    /**
     * Context path of the application.
     */
    private String contextPath;

    /**
     * Display name of the application.
     */
    private String displayName = "application";

    @NestedConfigurationProperty
    private ErrorProperties error = new ErrorProperties();

    /**
     * Path of the main dispatcher servlet.
     */
    private String servletPath = "/";

    /**
     * ServletContext parameters.
     */
    private final Map<String, String> contextParameters = new HashMap<String, String>();

    /**
     * If X-Forwarded-* headers should be applied to the HttpRequest.
     */
    private Boolean useForwardHeaders;

    /**
     * Value to use for the Server response header (no header is sent if empty).
     */
    private String serverHeader;

    /**
     * Maximum size in bytes of the HTTP message header.
     */
    private int maxHttpHeaderSize = 0; // bytes

    /**
     * Maximum size in bytes of the HTTP post content.
     */
    private int maxHttpPostSize = 0; // bytes

    /**
     * Time in milliseconds that connectors will wait for another HTTP request before
     * closing the connection. When not set, the connector's container-specific default
     * will be used. Use a value of -1 to indicate no (i.e. infinite) timeout.
     */
    private Integer connectionTimeout;

    private Session session = new Session();

    @NestedConfigurationProperty
    private Ssl ssl;

    @NestedConfigurationProperty
    private Compression compression = new Compression();


    private final Tomcat tomcat = new Tomcat();

    private Environment environment;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        if (getPort() != null) {
            container.setPort(getPort());
        }
        if (getAddress() != null) {
            container.setAddress(getAddress());
        }
        if (getContextPath() != null) {
            container.setContextPath(getContextPath());
        }
        if (getDisplayName() != null) {
            container.setDisplayName(getDisplayName());
        }
        if (getSession().getTimeout() != null) {
            container.setSessionTimeout(getSession().getTimeout());
        }
        container.setPersistSession(getSession().isPersistent());
        container.setSessionStoreDir(getSession().getStoreDir());
        if (getSsl() != null) {
            container.setSsl(getSsl());
        }
        if (getCompression() != null) {
            container.setCompression(getCompression());
        }
        container.setServerHeader(getServerHeader());
        if (container instanceof TomcatEmbeddedServletContainerFactory) {
            getTomcat().customizeTomcat(this,
                    (TomcatEmbeddedServletContainerFactory) container);
        }

        container.addInitializers(new SessionConfiguringInitializer(this.session));
        container.addInitializers(new InitParameterConfiguringServletContextInitializer(
                getContextParameters()));
    }

    public String getServletMapping() {
        if (this.servletPath.equals("") || this.servletPath.equals("/")) {
            return "/";
        }
        if (this.servletPath.contains("*")) {
            return this.servletPath;
        }
        if (this.servletPath.endsWith("/")) {
            return this.servletPath + "*";
        }
        return this.servletPath + "/*";
    }

    public String getPath(String path) {
        String prefix = getServletPrefix();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return prefix + path;
    }

    public String getServletPrefix() {
        String result = this.servletPath;
        if (result.contains("*")) {
            result = result.substring(0, result.indexOf("*"));
        }
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public String[] getPathsArray(Collection<String> paths) {
        String[] result = new String[paths.size()];
        int i = 0;
        for (String path : paths) {
            result[i++] = getPath(path);
        }
        return result;
    }

    public String[] getPathsArray(String[] paths) {
        String[] result = new String[paths.length];
        int i = 0;
        for (String path : paths) {
            result[i++] = getPath(path);
        }
        return result;
    }

    public void setLoader(String value) {
        // no op to support Tomcat running as a traditional container (not embedded)
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = cleanContextPath(contextPath);
    }

    private String cleanContextPath(String contextPath) {
        if (StringUtils.hasText(contextPath) && contextPath.endsWith("/")) {
            return contextPath.substring(0, contextPath.length() - 1);
        }
        return contextPath;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public void setServletPath(String servletPath) {
        Assert.notNull(servletPath, "ServletPath must not be null");
        this.servletPath = servletPath;
    }

    public Map<String, String> getContextParameters() {
        return this.contextParameters;
    }

    public Boolean isUseForwardHeaders() {
        return this.useForwardHeaders;
    }

    public void setUseForwardHeaders(Boolean useForwardHeaders) {
        this.useForwardHeaders = useForwardHeaders;
    }

    public String getServerHeader() {
        return this.serverHeader;
    }

    public void setServerHeader(String serverHeader) {
        this.serverHeader = serverHeader;
    }

    public int getMaxHttpHeaderSize() {
        return this.maxHttpHeaderSize;
    }

    public void setMaxHttpHeaderSize(int maxHttpHeaderSize) {
        this.maxHttpHeaderSize = maxHttpHeaderSize;
    }

    @Deprecated
    @DeprecatedConfigurationProperty(
            reason = "Use dedicated property for each container.")
    public int getMaxHttpPostSize() {
        return this.maxHttpPostSize;
    }

    @Deprecated
    public void setMaxHttpPostSize(int maxHttpPostSize) {
        this.maxHttpPostSize = maxHttpPostSize;
        this.tomcat.setMaxHttpPostSize(maxHttpPostSize);
    }

    protected final boolean getOrDeduceUseForwardHeaders() {
        if (this.useForwardHeaders != null) {
            return this.useForwardHeaders;
        }
        return  false;
    }

    public Integer getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public ErrorProperties getError() {
        return this.error;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    public Compression getCompression() {
        return this.compression;
    }

    public Tomcat getTomcat() {
        return this.tomcat;
    }

    public static class Session {

        /**
         * Session timeout in seconds.
         */
        private Integer timeout;

        /**
         * Session tracking modes (one or more of the following: "cookie", "url", "ssl").
         */
        private Set<SessionTrackingMode> trackingModes;

        /**
         * Persist session data between restarts.
         */
        private boolean persistent;

        /**
         * Directory used to store session data.
         */
        private File storeDir;

        private Cookie cookie = new Cookie();

        public Cookie getCookie() {
            return this.cookie;
        }

        public Integer getTimeout() {
            return this.timeout;
        }

        public void setTimeout(Integer sessionTimeout) {
            this.timeout = sessionTimeout;
        }

        public Set<SessionTrackingMode> getTrackingModes() {
            return this.trackingModes;
        }

        public void setTrackingModes(Set<SessionTrackingMode> trackingModes) {
            this.trackingModes = trackingModes;
        }

        public boolean isPersistent() {
            return this.persistent;
        }

        public void setPersistent(boolean persistent) {
            this.persistent = persistent;
        }

        public File getStoreDir() {
            return this.storeDir;
        }

        public void setStoreDir(File storeDir) {
            this.storeDir = storeDir;
        }

        public static class Cookie {

            /**
             * Session cookie name.
             */
            private String name;

            /**
             * Domain for the session cookie.
             */
            private String domain;

            /**
             * Path of the session cookie.
             */
            private String path;

            /**
             * Comment for the session cookie.
             */
            private String comment;

            /**
             * "HttpOnly" flag for the session cookie.
             */
            private Boolean httpOnly;

            /**
             * "Secure" flag for the session cookie.
             */
            private Boolean secure;

            /**
             * Maximum age of the session cookie in seconds.
             */
            private Integer maxAge;

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDomain() {
                return this.domain;
            }

            public void setDomain(String domain) {
                this.domain = domain;
            }

            public String getPath() {
                return this.path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getComment() {
                return this.comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public Boolean getHttpOnly() {
                return this.httpOnly;
            }

            public void setHttpOnly(Boolean httpOnly) {
                this.httpOnly = httpOnly;
            }

            public Boolean getSecure() {
                return this.secure;
            }

            public void setSecure(Boolean secure) {
                this.secure = secure;
            }

            public Integer getMaxAge() {
                return this.maxAge;
            }

            public void setMaxAge(Integer maxAge) {
                this.maxAge = maxAge;
            }

        }

    }

    public static class Tomcat {

        /**
         * Access log configuration.
         */
        private final Accesslog accesslog = new Accesslog();

        /**
         * Regular expression that matches proxies that are to be trusted.
         */
        private String internalProxies = "10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" // 10/8
                + "192\\.168\\.\\d{1,3}\\.\\d{1,3}|" // 192.168/16
                + "169\\.254\\.\\d{1,3}\\.\\d{1,3}|" // 169.254/16
                + "127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" // 127/8
                + "172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|" // 172.16/12
                + "172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|"
                + "172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|" //
                + "0:0:0:0:0:0:0:1|::1";

        /**
         * Header that holds the incoming protocol, usually named "X-Forwarded-Proto".
         */
        private String protocolHeader;

        /**
         * Value of the protocol header that indicates that the incoming request uses SSL.
         */
        private String protocolHeaderHttpsValue = "https";

        /**
         * Name of the HTTP header used to override the original port value.
         */
        private String portHeader = "X-Forwarded-Port";

        /**
         * Name of the http header from which the remote ip is extracted..
         */
        private String remoteIpHeader;

        /**
         * Tomcat base directory. If not specified a temporary directory will be used.
         */
        private File basedir;

        /**
         * Delay in seconds between the invocation of backgroundProcess methods.
         */
        private int backgroundProcessorDelay = 10; // seconds

        /**
         * Maximum amount of worker threads.
         */
        private int maxThreads = 200; // Number of threads in protocol handler

        /**
         * Minimum amount of worker threads.
         */
        private int minSpareThreads = 10; // Minimum spare threads in protocol handler

        /**
         * Maximum size in bytes of the HTTP post content.
         */
        private int maxHttpPostSize = 2097152; // bytes

        /**
         * Maximum size in bytes of the HTTP message header.
         */
        private int maxHttpHeaderSize = 0; // bytes

        /**
         * Whether requests to the context root should be redirected by appending a / to
         * the path.
         */
        private Boolean redirectContextRoot = true;

        /**
         * Character encoding to use to decode the URI.
         */
        private Charset uriEncoding = Charset.forName("UTF-8");

        /**
         * Maximum number of connections that the server will accept and process at any
         * given time. Once the limit has been reached, the operating system may still
         * accept connections based on the "acceptCount" property.
         */
        private int maxConnections = 10000;

        /**
         * Maximum queue length for incoming connection requests when all possible request
         * processing threads are in use.
         */
        private int acceptCount = 100;

        /**
         * Comma-separated list of additional patterns that match jars to ignore for TLD
         * scanning. The special '?' and '*' characters can be used in the pattern to
         * match one and only one character and zero or more characters respectively.
         */
        private List<String> additionalTldSkipPatterns = new ArrayList<String>();

        public int getMaxThreads() {
            return this.maxThreads;
        }

        public void setMaxThreads(int maxThreads) {
            this.maxThreads = maxThreads;
        }

        public int getMinSpareThreads() {
            return this.minSpareThreads;
        }

        public void setMinSpareThreads(int minSpareThreads) {
            this.minSpareThreads = minSpareThreads;
        }

        public int getMaxHttpPostSize() {
            return this.maxHttpPostSize;
        }

        public void setMaxHttpPostSize(int maxHttpPostSize) {
            this.maxHttpPostSize = maxHttpPostSize;
        }

        public Accesslog getAccesslog() {
            return this.accesslog;
        }

        public int getBackgroundProcessorDelay() {
            return this.backgroundProcessorDelay;
        }

        public void setBackgroundProcessorDelay(int backgroundProcessorDelay) {
            this.backgroundProcessorDelay = backgroundProcessorDelay;
        }

        public File getBasedir() {
            return this.basedir;
        }

        public void setBasedir(File basedir) {
            this.basedir = basedir;
        }

        public String getInternalProxies() {
            return this.internalProxies;
        }

        public void setInternalProxies(String internalProxies) {
            this.internalProxies = internalProxies;
        }

        public String getProtocolHeader() {
            return this.protocolHeader;
        }

        public void setProtocolHeader(String protocolHeader) {
            this.protocolHeader = protocolHeader;
        }

        public String getProtocolHeaderHttpsValue() {
            return this.protocolHeaderHttpsValue;
        }

        public void setProtocolHeaderHttpsValue(String protocolHeaderHttpsValue) {
            this.protocolHeaderHttpsValue = protocolHeaderHttpsValue;
        }

        public String getPortHeader() {
            return this.portHeader;
        }

        public void setPortHeader(String portHeader) {
            this.portHeader = portHeader;
        }

        public Boolean getRedirectContextRoot() {
            return this.redirectContextRoot;
        }

        public void setRedirectContextRoot(Boolean redirectContextRoot) {
            this.redirectContextRoot = redirectContextRoot;
        }

        public String getRemoteIpHeader() {
            return this.remoteIpHeader;
        }

        public void setRemoteIpHeader(String remoteIpHeader) {
            this.remoteIpHeader = remoteIpHeader;
        }

        public Charset getUriEncoding() {
            return this.uriEncoding;
        }

        public void setUriEncoding(Charset uriEncoding) {
            this.uriEncoding = uriEncoding;
        }

        public int getMaxConnections() {
            return this.maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getAcceptCount() {
            return this.acceptCount;
        }

        public void setAcceptCount(int acceptCount) {
            this.acceptCount = acceptCount;
        }

        public List<String> getAdditionalTldSkipPatterns() {
            return this.additionalTldSkipPatterns;
        }

        public void setAdditionalTldSkipPatterns(List<String> additionalTldSkipPatterns) {
            this.additionalTldSkipPatterns = additionalTldSkipPatterns;
        }

        void customizeTomcat(ServerProperties serverProperties,
                             TomcatEmbeddedServletContainerFactory factory) {
            if (getBasedir() != null) {
                factory.setBaseDirectory(getBasedir());
            }
            factory.setBackgroundProcessorDelay(Tomcat.this.backgroundProcessorDelay);
            customizeRemoteIpValve(serverProperties, factory);
            if (this.maxThreads > 0) {
                customizeMaxThreads(factory);
            }
            if (this.minSpareThreads > 0) {
                customizeMinThreads(factory);
            }
            int maxHttpHeaderSize = (serverProperties.getMaxHttpHeaderSize() > 0)
                    ? serverProperties.getMaxHttpHeaderSize() : this.maxHttpHeaderSize;
            if (maxHttpHeaderSize > 0) {
                customizeMaxHttpHeaderSize(factory, maxHttpHeaderSize);
            }
            if (this.maxHttpPostSize != 0) {
                customizeMaxHttpPostSize(factory, this.maxHttpPostSize);
            }
            if (this.accesslog.enabled) {
                customizeAccessLog(factory);
            }
            if (getUriEncoding() != null) {
                factory.setUriEncoding(getUriEncoding());
            }
            if (serverProperties.getConnectionTimeout() != null) {
                customizeConnectionTimeout(factory,
                        serverProperties.getConnectionTimeout());
            }
            if (this.redirectContextRoot != null) {
                customizeRedirectContextRoot(factory, this.redirectContextRoot);
            }
            if (this.maxConnections > 0) {
                customizeMaxConnections(factory);
            }
            if (this.acceptCount > 0) {
                customizeAcceptCount(factory);
            }
            if (!ObjectUtils.isEmpty(this.additionalTldSkipPatterns)) {
                factory.getTldSkipPatterns().addAll(this.additionalTldSkipPatterns);
            }
            if (serverProperties.getError()
                    .getIncludeStacktrace() == ErrorProperties.IncludeStacktrace.NEVER) {
                customizeErrorReportValve(factory);
            }
            final Session.Cookie cookie = serverProperties.getSession().getCookie();
            if (cookie.getHttpOnly() != null) {
                factory.addContextCustomizers(new TomcatContextCustomizer() {

                    @Override
                    public void customize(Context context) {
                        context.setUseHttpOnly(cookie.getHttpOnly());
                    }

                });
            }
        }

        private void customizeErrorReportValve(
                TomcatEmbeddedServletContainerFactory factory) {
            factory.addContextCustomizers(new TomcatContextCustomizer() {

                @Override
                public void customize(Context context) {
                    ErrorReportValve valve = new ErrorReportValve();
                    valve.setShowServerInfo(false);
                    valve.setShowReport(false);
                    context.getParent().getPipeline().addValve(valve);
                }

            });
        }

        private void customizeAcceptCount(TomcatEmbeddedServletContainerFactory factory) {
            factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                @Override
                @SuppressWarnings("deprecation")
                public void customize(Connector connector) {
                    ProtocolHandler handler = connector.getProtocolHandler();
                    if (handler instanceof AbstractProtocol) {
                        AbstractProtocol<?> protocol = (AbstractProtocol<?>) handler;
                        protocol.setBacklog(Tomcat.this.acceptCount);
                    }
                }

            });
        }

        private void customizeMaxConnections(
                TomcatEmbeddedServletContainerFactory factory) {
            factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                @Override
                public void customize(Connector connector) {
                    ProtocolHandler handler = connector.getProtocolHandler();
                    if (handler instanceof AbstractProtocol) {
                        AbstractProtocol<?> protocol = (AbstractProtocol<?>) handler;
                        protocol.setMaxConnections(Tomcat.this.maxConnections);
                    }
                }

            });
        }

        private void customizeConnectionTimeout(
                TomcatEmbeddedServletContainerFactory factory,
                final int connectionTimeout) {
            factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                @Override
                public void customize(Connector connector) {
                    ProtocolHandler handler = connector.getProtocolHandler();
                    if (handler instanceof AbstractProtocol) {
                        AbstractProtocol<?> protocol = (AbstractProtocol<?>) handler;
                        protocol.setConnectionTimeout(connectionTimeout);
                    }
                }

            });
        }

        private void customizeRemoteIpValve(ServerProperties properties,
                                            TomcatEmbeddedServletContainerFactory factory) {
            String protocolHeader = getProtocolHeader();
            String remoteIpHeader = getRemoteIpHeader();
            // For back compatibility the valve is also enabled if protocol-header is set
            if (StringUtils.hasText(protocolHeader) || StringUtils.hasText(remoteIpHeader)
                    || properties.getOrDeduceUseForwardHeaders()) {
                RemoteIpValve valve = new RemoteIpValve();
                valve.setProtocolHeader(StringUtils.hasLength(protocolHeader)
                        ? protocolHeader : "X-Forwarded-Proto");
                if (StringUtils.hasLength(remoteIpHeader)) {
                    valve.setRemoteIpHeader(remoteIpHeader);
                }
                // The internal proxies default to a white list of "safe" internal IP
                // addresses
                valve.setInternalProxies(getInternalProxies());
                valve.setPortHeader(getPortHeader());
                valve.setProtocolHeaderHttpsValue(getProtocolHeaderHttpsValue());
                // ... so it's safe to add this valve by default.
                factory.addEngineValves(valve);
            }
        }

        @SuppressWarnings("rawtypes")
        private void customizeMaxThreads(TomcatEmbeddedServletContainerFactory factory) {
            factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
                @Override
                public void customize(Connector connector) {

                    ProtocolHandler handler = connector.getProtocolHandler();
                    if (handler instanceof AbstractProtocol) {
                        AbstractProtocol protocol = (AbstractProtocol) handler;
                        protocol.setMaxThreads(Tomcat.this.maxThreads);
                    }

                }
            });
        }

        @SuppressWarnings("rawtypes")
        private void customizeMinThreads(TomcatEmbeddedServletContainerFactory factory) {
            factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
                @Override
                public void customize(Connector connector) {

                    ProtocolHandler handler = connector.getProtocolHandler();
                    if (handler instanceof AbstractProtocol) {
                        AbstractProtocol protocol = (AbstractProtocol) handler;
                        protocol.setMinSpareThreads(Tomcat.this.minSpareThreads);
                    }

                }
            });
        }

        @SuppressWarnings("rawtypes")
        private void customizeMaxHttpHeaderSize(
                TomcatEmbeddedServletContainerFactory factory,
                final int maxHttpHeaderSize) {
            factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                @Override
                public void customize(Connector connector) {
                    ProtocolHandler handler = connector.getProtocolHandler();
                    if (handler instanceof AbstractHttp11Protocol) {
                        AbstractHttp11Protocol protocol = (AbstractHttp11Protocol) handler;
                        protocol.setMaxHttpHeaderSize(maxHttpHeaderSize);
                    }
                }

            });
        }

        private void customizeMaxHttpPostSize(
                TomcatEmbeddedServletContainerFactory factory,
                final int maxHttpPostSize) {
            factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                @Override
                public void customize(Connector connector) {
                    connector.setMaxPostSize(maxHttpPostSize);
                }

            });
        }

        private void customizeAccessLog(TomcatEmbeddedServletContainerFactory factory) {
            AccessLogValve valve = new AccessLogValve();
            valve.setPattern(this.accesslog.getPattern());
            valve.setDirectory(this.accesslog.getDirectory());
            valve.setPrefix(this.accesslog.getPrefix());
            valve.setSuffix(this.accesslog.getSuffix());
            valve.setRenameOnRotate(this.accesslog.isRenameOnRotate());
            valve.setRequestAttributesEnabled(
                    this.accesslog.isRequestAttributesEnabled());
            valve.setRotatable(this.accesslog.isRotate());
            valve.setBuffered(this.accesslog.isBuffered());
            valve.setFileDateFormat(this.accesslog.getFileDateFormat());
            factory.addEngineValves(valve);
        }

        private void customizeRedirectContextRoot(
                TomcatEmbeddedServletContainerFactory factory,
                final boolean redirectContextRoot) {
            factory.addContextCustomizers(new TomcatContextCustomizer() {

                @Override
                public void customize(Context context) {
                    try {
                        context.setMapperContextRootRedirectEnabled(redirectContextRoot);
                    }
                    catch (NoSuchMethodError ex) {
                        // Tomcat 7. Continue.
                    }
                }

            });
        }

        public static class Accesslog {

            /**
             * Enable access log.
             */
            private boolean enabled = false;

            /**
             * Format pattern for access logs.
             */
            private String pattern = "common";

            /**
             * Directory in which log files are created. Can be relative to the tomcat
             * base dir or absolute.
             */
            private String directory = "logs";

            /**
             * Log file name prefix.
             */
            protected String prefix = "access_log";

            /**
             * Log file name suffix.
             */
            private String suffix = ".log";

            /**
             * Enable access log rotation.
             */
            private boolean rotate = true;

            /**
             * Defer inclusion of the date stamp in the file name until rotate time.
             */
            private boolean renameOnRotate = false;

            /**
             * Date format to place in log file name.
             */
            private String fileDateFormat = ".yyyy-MM-dd";

            /**
             * Set request attributes for IP address, Hostname, protocol and port used for
             * the request.
             */
            private boolean requestAttributesEnabled = false;

            /**
             * Buffer output such that it is only flushed periodically.
             */
            private boolean buffered = true;

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getPattern() {
                return this.pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }

            public String getDirectory() {
                return this.directory;
            }

            public void setDirectory(String directory) {
                this.directory = directory;
            }

            public String getPrefix() {
                return this.prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }

            public String getSuffix() {
                return this.suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public boolean isRotate() {
                return this.rotate;
            }

            public void setRotate(boolean rotate) {
                this.rotate = rotate;
            }

            public boolean isRenameOnRotate() {
                return this.renameOnRotate;
            }

            public void setRenameOnRotate(boolean renameOnRotate) {
                this.renameOnRotate = renameOnRotate;
            }

            public String getFileDateFormat() {
                return this.fileDateFormat;
            }

            public void setFileDateFormat(String fileDateFormat) {
                this.fileDateFormat = fileDateFormat;
            }

            public boolean isRequestAttributesEnabled() {
                return this.requestAttributesEnabled;
            }

            public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
                this.requestAttributesEnabled = requestAttributesEnabled;
            }

            public boolean isBuffered() {
                return this.buffered;
            }

            public void setBuffered(boolean buffered) {
                this.buffered = buffered;
            }

        }

    }

    /**
     * {@link ServletContextInitializer} to apply appropriate parts of the {@link Session}
     * configuration.
     */
    private static class SessionConfiguringInitializer
            implements ServletContextInitializer {

        private final Session session;

        SessionConfiguringInitializer(Session session) {
            this.session = session;
        }

        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            if (this.session.getTrackingModes() != null) {
                servletContext.setSessionTrackingModes(this.session.getTrackingModes());
            }
            configureSessionCookie(servletContext.getSessionCookieConfig());
        }

        private void configureSessionCookie(SessionCookieConfig config) {
            Session.Cookie cookie = this.session.getCookie();
            if (cookie.getName() != null) {
                config.setName(cookie.getName());
            }
            if (cookie.getDomain() != null) {
                config.setDomain(cookie.getDomain());
            }
            if (cookie.getPath() != null) {
                config.setPath(cookie.getPath());
            }
            if (cookie.getComment() != null) {
                config.setComment(cookie.getComment());
            }
            if (cookie.getHttpOnly() != null) {
                config.setHttpOnly(cookie.getHttpOnly());
            }
            if (cookie.getSecure() != null) {
                config.setSecure(cookie.getSecure());
            }
            if (cookie.getMaxAge() != null) {
                config.setMaxAge(cookie.getMaxAge());
            }
        }

    }

}
