package com.rocket.summer.framework.web.bind;

import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * {@link ServletRequestBindingException} subclass that indicates an unsatisfied
 * parameter condition, as typically expressed using an <code>@RequestMapping</code>
 * annotation at the <code>@Controller</code> type level.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.web.bind.annotation.RequestMapping#params()
 */
public class UnsatisfiedServletRequestParameterException extends ServletRequestBindingException {

    private final String[] paramConditions;

    private final Map<String, String[]> actualParams;


    /**
     * Create a new UnsatisfiedServletRequestParameterException.
     * @param paramConditions the parameter conditions that have been violated
     * @param actualParams the actual parameter Map associated with the ServletRequest
     */
    @SuppressWarnings("unchecked")
    public UnsatisfiedServletRequestParameterException(String[] paramConditions, Map actualParams) {
        super("");
        this.paramConditions = paramConditions;
        this.actualParams = (Map<String, String[]>) actualParams;
    }


    @Override
    public String getMessage() {
        return "Parameter conditions \"" + StringUtils.arrayToDelimitedString(this.paramConditions, ", ") +
                "\" not met for actual request parameters: " + requestParameterMapToString(this.actualParams);
    }

    private static String requestParameterMapToString(Map<String, String[]> actualParams) {
        StringBuilder result = new StringBuilder();
        for (Iterator<Map.Entry<String, String[]>> it = actualParams.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String[]> entry = it.next();
            result.append(entry.getKey()).append('=').append(ObjectUtils.nullSafeToString(entry.getValue()));
            if (it.hasNext()) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    /**
     * Return the parameter conditions that have been violated.
     * @see com.rocket.summer.framework.web.bind.annotation.RequestMapping#params()
     */
    public final String[] getParamConditions() {
        return this.paramConditions;
    }

    /**
     * Return the actual parameter Map associated with the ServletRequest.
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    public final Map<String, String[]> getActualParams() {
        return this.actualParams;
    }

}
