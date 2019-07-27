package com.rocket.summer.framework.web.bind;

/**
 * {@link ServletRequestBindingException} subclass that indicates a missing parameter.
 *
 * @author Juergen Hoeller
 * @since 2.0.2
 */
public class MissingServletRequestParameterException extends ServletRequestBindingException {

    private final String parameterName;

    private final String parameterType;


    /**
     * Constructor for MissingServletRequestParameterException.
     * @param parameterName the name of the missing parameter
     * @param parameterType the expected type of the missing parameter
     */
    public MissingServletRequestParameterException(String parameterName, String parameterType) {
        super("");
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }


    @Override
    public String getMessage() {
        return "Required " + this.parameterType + " parameter '" + this.parameterName + "' is not present";
    }

    /**
     * Return the name of the offending parameter.
     */
    public final String getParameterName() {
        return this.parameterName;
    }

    /**
     * Return the expected type of the offending parameter.
     */
    public final String getParameterType() {
        return this.parameterType;
    }

}
