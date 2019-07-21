package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Event raised when a request is handled within an ApplicationContext.
 *
 * <p>Supported by Spring's own FrameworkServlet (through a specific
 * ServletRequestHandledEvent subclass), but can also be raised by any
 * other web component. Used, for example, by Spring's out-of-the-box
 * PerformanceMonitorListener.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since January 17, 2001
 * @see ServletRequestHandledEvent
 * @see org.springframework.web.servlet.FrameworkServlet
 * @see org.springframework.context.ApplicationContext#publishEvent
 */
public class RequestHandledEvent extends ApplicationEvent {

    /** Session id that applied to the request, if any */
    private String sessionId;

    /** Usually the UserPrincipal */
    private String userName;

    /** Request processing time */
    private final long processingTimeMillis;

    /** Cause of failure, if any */
    private Throwable failureCause;


    /**
     * Create a new RequestHandledEvent with session information.
     * @param source the component that published the event
     * @param sessionId the id of the HTTP session, if any
     * @param userName the name of the user that was associated with the
     * request, if any (usually the UserPrincipal)
     * @param processingTimeMillis the processing time of the request in milliseconds
     */
    public RequestHandledEvent(Object source, String sessionId, String userName, long processingTimeMillis) {
        super(source);
        this.sessionId = sessionId;
        this.userName = userName;
        this.processingTimeMillis = processingTimeMillis;
    }

    /**
     * Create a new RequestHandledEvent with session information.
     * @param source the component that published the event
     * @param sessionId the id of the HTTP session, if any
     * @param userName the name of the user that was associated with the
     * request, if any (usually the UserPrincipal)
     * @param processingTimeMillis the processing time of the request in milliseconds
     * @param failureCause the cause of failure, if any
     */
    public RequestHandledEvent(
            Object source, String sessionId, String userName, long processingTimeMillis, Throwable failureCause) {

        this(source, sessionId, userName, processingTimeMillis);
        this.failureCause = failureCause;
    }


    /**
     * Return the processing time of the request in milliseconds.
     */
    public long getProcessingTimeMillis() {
        return this.processingTimeMillis;
    }

    /**
     * Return the id of the HTTP session, if any.
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Return the name of the user that was associated with the request
     * (usually the UserPrincipal).
     * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Return whether the request failed.
     */
    public boolean wasFailure() {
        return (this.failureCause != null);
    }

    /**
     * Return the cause of failure, if any.
     */
    public Throwable getFailureCause() {
        return this.failureCause;
    }


    /**
     * Return a short description of this event, only involving
     * the most important context data.
     */
    public String getShortDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("session=[").append(this.sessionId).append("]; ");
        sb.append("user=[").append(this.userName).append("]; ");
        return sb.toString();
    }

    /**
     * Return a full description of this event, involving
     * all available context data.
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("session=[").append(this.sessionId).append("]; ");
        sb.append("user=[").append(this.userName).append("]; ");
        sb.append("time=[").append(this.processingTimeMillis).append("ms]; ");
        sb.append("status=[");
        if (!wasFailure()) {
            sb.append("OK");
        }
        else {
            sb.append("failed: ").append(this.failureCause);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public String toString() {
        return ("RequestHandledEvent: " + getDescription());
    }

}
