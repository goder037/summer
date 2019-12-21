package com.rocket.summer.framework.web.bind.support;

import com.rocket.summer.framework.beans.MutablePropertyValues;
import com.rocket.summer.framework.validation.BindException;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.WebRequest;
import com.rocket.summer.framework.web.multipart.MultipartRequest;

/**
 * Special {@link com.rocket.summer.framework.validation.DataBinder} to perform data binding
 * from web request parameters to JavaBeans, including support for multipart files.
 *
 * <p>See the DataBinder/WebDataBinder superclasses for customization options,
 * which include specifying allowed/required fields, and registering custom
 * property editors.
 *
 * <p>Can also used for manual data binding in custom web controllers or interceptors
 * that build on Spring's {@link com.rocket.summer.framework.web.context.request.WebRequest}
 * abstraction: e.g. in a {@link com.rocket.summer.framework.web.context.request.WebRequestInterceptor}
 * implementation. Simply instantiate a WebRequestDataBinder for each binding
 * process, and invoke <code>bind</code> with the current WebRequest as argument:
 *
 * <pre class="code">
 * MyBean myBean = new MyBean();
 * // apply binder to custom target object
 * WebRequestDataBinder binder = new WebRequestDataBinder(myBean);
 * // register custom editors, if desired
 * binder.registerCustomEditor(...);
 * // trigger actual binding of request parameters
 * binder.bind(request);
 * // optionally evaluate binding errors
 * Errors errors = binder.getErrors();
 * ...</pre>
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see #bind(com.rocket.summer.framework.web.context.request.WebRequest)
 * @see #registerCustomEditor
 * @see #setAllowedFields
 * @see #setRequiredFields
 * @see #setFieldMarkerPrefix
 */
public class WebRequestDataBinder extends WebDataBinder {

    /**
     * Create a new WebRequestDataBinder instance, with default object name.
     * @param target the target object to bind onto (or <code>null</code>
     * if the binder is just used to convert a plain parameter value)
     * @see #DEFAULT_OBJECT_NAME
     */
    public WebRequestDataBinder(Object target) {
        super(target);
    }

    /**
     * Create a new WebRequestDataBinder instance.
     * @param target the target object to bind onto (or <code>null</code>
     * if the binder is just used to convert a plain parameter value)
     * @param objectName the name of the target object
     */
    public WebRequestDataBinder(Object target, String objectName) {
        super(target, objectName);
    }


    /**
     * Bind the parameters of the given request to this binder's target,
     * also binding multipart files in case of a multipart request.
     * <p>This call can create field errors, representing basic binding
     * errors like a required field (code "required"), or type mismatch
     * between value and bean property (code "typeMismatch").
     * <p>Multipart files are bound via their parameter name, just like normal
     * HTTP parameters: i.e. "uploadedFile" to an "uploadedFile" bean property,
     * invoking a "setUploadedFile" setter method.
     * <p>The type of the target property for a multipart file can be MultipartFile,
     * byte[], or String. The latter two receive the contents of the uploaded file;
     * all metadata like original file name, content type, etc are lost in those cases.
     * @param request request with parameters to bind (can be multipart)
     * @see com.rocket.summer.framework.web.multipart.MultipartRequest
     * @see com.rocket.summer.framework.web.multipart.MultipartFile
     * @see #bindMultipartFiles
     * @see #bind(com.rocket.summer.framework.beans.PropertyValues)
     */
    public void bind(WebRequest request) {
        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
        if (request instanceof NativeWebRequest) {
            MultipartRequest multipartRequest = ((NativeWebRequest) request).getNativeRequest(MultipartRequest.class);
            if (multipartRequest != null) {
                bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
            }
        }
        doBind(mpvs);
    }

    /**
     * Treats errors as fatal.
     * <p>Use this method only if it's an error if the input isn't valid.
     * This might be appropriate if all input is from dropdowns, for example.
     * @throws BindException if binding errors have been encountered
     */
    public void closeNoCatch() throws BindException {
        if (getBindingResult().hasErrors()) {
            throw new BindException(getBindingResult());
        }
    }

}