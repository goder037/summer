package com.rocket.summer.framework.web.bind;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link ServletRequestBindingException} subclass that indicates an unsatisfied
 * parameter condition, as typically expressed using an {@code @RequestMapping}
 * annotation at the {@code @Controller} type level.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.web.bind.annotation.RequestMapping#params()
 */
@SuppressWarnings("serial")
public class UnsatisfiedServletRequestParameterException extends ServletRequestBindingException {

    private final List<String[]> paramConditions;

    private final Map<String, String[]> actualParams;


    /**
     * Create a new UnsatisfiedServletRequestParameterException.
     * @param paramConditions the parameter conditions that have been violated
     * @param actualParams the actual parameter Map associated with the ServletRequest
     */
    public UnsatisfiedServletRequestParameterException(String[] paramConditions, Map<String, String[]> actualParams) {
        super("");
        this.paramConditions = Arrays.<String[]>asList(paramConditions);
        this.actualParams = actualParams;
    }

    /**
     * Create a new UnsatisfiedServletRequestParameterException.
     * @param paramConditions all sets of parameter conditions that have been violated
     * @param actualParams the actual parameter Map associated with the ServletRequest
     * @since 4.2
     */
    public UnsatisfiedServletRequestParameterException(List<String[]> paramConditions,
                                                       Map<String, String[]> actualParams) {

        super("");
        Assert.notEmpty(paramConditions, "Parameter conditions must not be empty");
        this.paramConditions = paramConditions;
        this.actualParams = actualParams;
    }


    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Parameter conditions ");
        int i = 0;
        for (String[] conditions : this.paramConditions) {
            if (i > 0) {
                sb.append(" OR ");
            }
            sb.append("\"");
            sb.append(StringUtils.arrayToDelimitedString(conditions, ", "));
            sb.append("\"");
            i++;
        }
        sb.append(" not met for actual request parameters: ");
        sb.append(requestParameterMapToString(this.actualParams));
        return sb.toString();
    }

    /**
     * Return the parameter conditions that have been violated or the first group
     * in case of multiple groups.
     * @see com.rocket.summer.framework.web.bind.annotation.RequestMapping#params()
     */
    public final String[] getParamConditions() {
        return this.paramConditions.get(0);
    }

    /**
     * Return all parameter condition groups that have been violated.
     * @see com.rocket.summer.framework.web.bind.annotation.RequestMapping#params()
     * @since 4.2
     */
    public final List<String[]> getParamConditionGroups() {
        return this.paramConditions;
    }

    /**
     * Return the actual parameter Map associated with the ServletRequest.
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    public final Map<String, String[]> getActualParams() {
        return this.actualParams;
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

}
