package com.rocket.summer.framework.web.servlet.support;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * A contract for inspecting and potentially modifying request data values such
 * as URL query parameters or form field values before they are rendered by a
 * view or before a redirect.
 *
 * <p>Implementations may use this contract for example as part of a solution
 * to provide data integrity, confidentiality, protection against cross-site
 * request forgery (CSRF), and others or for other tasks such as automatically
 * adding a hidden field to all forms and URLs.
 *
 * <p>View technologies that support this contract can obtain an instance to
 * delegate to via {@link RequestContext#getRequestDataValueProcessor()}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public interface RequestDataValueProcessor {

    /**
     * Invoked when a new form action is rendered.
     * @param request the current request
     * @param action the form action
     * @param httpMethod the form HTTP method
     * @return the action to use, possibly modified
     */
    String processAction(HttpServletRequest request, String action, String httpMethod);

    /**
     * Invoked when a form field value is rendered.
     * @param request the current request
     * @param name the form field name
     * @param value the form field value
     * @param type the form field type ("text", "hidden", etc.)
     * @return the form field value to use, possibly modified
     */
    String processFormFieldValue(HttpServletRequest request, String name, String value, String type);

    /**
     * Invoked after all form fields have been rendered.
     * @param request the current request
     * @return additional hidden form fields to be added, or {@code null}
     */
    Map<String, String> getExtraHiddenFields(HttpServletRequest request);

    /**
     * Invoked when a URL is about to be rendered or redirected to.
     * @param request the current request
     * @param url the URL value
     * @return the URL to use, possibly modified
     */
    String processUrl(HttpServletRequest request, String url);

}

