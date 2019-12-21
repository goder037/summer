package com.rocket.summer.framework.web.servlet.mvc.condition;

import com.rocket.summer.framework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * A logical disjunction (' || ') request condition that matches a request
 * against a set of {@link RequestMethod}s.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class RequestMethodsRequestCondition extends AbstractRequestCondition<RequestMethodsRequestCondition> {

    private final Set<RequestMethod> methods;

    /**
     * Create a new instance with the given request methods.
     * @param requestMethods 0 or more HTTP request methods;
     * 		if, 0 the condition will match to every request.
     */
    public RequestMethodsRequestCondition(RequestMethod... requestMethods) {
        this(asList(requestMethods));
    }

    private static List<RequestMethod> asList(RequestMethod... requestMethods) {
        return requestMethods != null ? Arrays.asList(requestMethods) : Collections.<RequestMethod>emptyList();
    }

    /**
     * Private constructor.
     */
    private RequestMethodsRequestCondition(Collection<RequestMethod> requestMethods) {
        this.methods = Collections.unmodifiableSet(new LinkedHashSet<RequestMethod>(requestMethods));
    }

    /**
     * Returns all {@link RequestMethod}s contained in this condition.
     */
    public Set<RequestMethod> getMethods() {
        return methods;
    }

    @Override
    protected Collection<RequestMethod> getContent() {
        return methods;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    /**
     * Returns a new instance with a union of the HTTP request methods
     * from "this" and the "other" instance.
     */
    public RequestMethodsRequestCondition combine(RequestMethodsRequestCondition other) {
        Set<RequestMethod> set = new LinkedHashSet<RequestMethod>(this.methods);
        set.addAll(other.methods);
        return new RequestMethodsRequestCondition(set);
    }

    /**
     * Checks if any of the HTTP request methods match the given request and returns
     * an instance that contains the matching request method only.
     * @param request the current request
     * @return the same instance if the condition contains no request method;
     * 		or a new condition with the matching request method;
     * 		or {@code null} if no request methods match.
     */
    public RequestMethodsRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (methods.isEmpty()) {
            return this;
        }
        RequestMethod incomingRequestMethod = RequestMethod.valueOf(request.getMethod());
        for (RequestMethod method : methods) {
            if (method.equals(incomingRequestMethod)) {
                return new RequestMethodsRequestCondition(method);
            }
        }
        return null;
    }

    /**
     * Returns:
     * <ul>
     * 	<li>0 if the two conditions contain the same number of HTTP request methods.
     * 	<li>Less than 0 if "this" instance has an HTTP request method but "other" doesn't.
     * 	<li>Greater than 0 "other" has an HTTP request method but "this" doesn't.
     * </ul>
     *
     * <p>It is assumed that both instances have been obtained via
     * {@link #getMatchingCondition(HttpServletRequest)} and therefore each instance
     * contains the matching HTTP request method only or is otherwise empty.
     */
    public int compareTo(RequestMethodsRequestCondition other, HttpServletRequest request) {
        return other.methods.size() - this.methods.size();
    }

}

