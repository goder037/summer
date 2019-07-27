package com.rocket.summer.framework.web.bind;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.validation.BindingResult;
import com.rocket.summer.framework.validation.ObjectError;

/**
 * Exception to be thrown when validation on an argument annotated with {@code @Valid} fails.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class MethodArgumentNotValidException extends Exception {

    private final MethodParameter parameter;

    private final BindingResult bindingResult;


    /**
     * Constructor for {@link MethodArgumentNotValidException}.
     * @param parameter the parameter that failed validation
     * @param bindingResult the results of the validation
     */
    public MethodArgumentNotValidException(MethodParameter parameter, BindingResult bindingResult) {
        this.parameter = parameter;
        this.bindingResult = bindingResult;
    }

    /**
     * Return the method parameter that failed validation.
     */
    public MethodParameter getParameter() {
        return this.parameter;
    }

    /**
     * Return the results of the failed validation.
     */
    public BindingResult getBindingResult() {
        return this.bindingResult;
    }


    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Validation failed for argument at index ")
                .append(this.parameter.getParameterIndex()).append(" in method: ")
                .append(this.parameter.getMethod().toGenericString())
                .append(", with ").append(this.bindingResult.getErrorCount()).append(" error(s): ");
        for (ObjectError error : this.bindingResult.getAllErrors()) {
            sb.append("[").append(error).append("] ");
        }
        return sb.toString();
    }

}
