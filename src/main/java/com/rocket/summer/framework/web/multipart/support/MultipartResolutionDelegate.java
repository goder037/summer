package com.rocket.summer.framework.web.multipart.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.web.multipart.MultipartException;
import com.rocket.summer.framework.web.multipart.MultipartFile;
import com.rocket.summer.framework.web.multipart.MultipartHttpServletRequest;
import com.rocket.summer.framework.web.util.WebUtils;

/**
 * A common delegate for {@code HandlerMethodArgumentResolver} implementations
 * which need to resolve {@link MultipartFile} and {@link Part} arguments.
 *
 * @author Juergen Hoeller
 * @since 4.3
 */
public abstract class MultipartResolutionDelegate {

    public static final Object UNRESOLVABLE = new Object();


    private static Class<?> servletPartClass = null;

    static {
        try {
            servletPartClass = ClassUtils.forName("javax.servlet.http.Part",
                    MultipartResolutionDelegate.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            // Servlet 3.0 javax.servlet.http.Part type not available -
            // Part references simply not supported then.
        }
    }


    public static boolean isMultipartRequest(HttpServletRequest request) {
        return (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null ||
                isMultipartContent(request));
    }

    private static boolean isMultipartContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
    }

    static MultipartHttpServletRequest asMultipartHttpServletRequest(HttpServletRequest request) {
        MultipartHttpServletRequest unwrapped = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (unwrapped != null) {
            return unwrapped;
        }
        return adaptToMultipartHttpServletRequest(request);
    }

    private static MultipartHttpServletRequest adaptToMultipartHttpServletRequest(HttpServletRequest request) {
        if (servletPartClass != null) {
            // Servlet 3.0 available ..
            return new StandardMultipartHttpServletRequest(request);
        }
        throw new MultipartException("Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
    }


    public static boolean isMultipartArgument(MethodParameter parameter) {
        Class<?> paramType = parameter.getNestedParameterType();
        return (MultipartFile.class == paramType ||
                isMultipartFileCollection(parameter) || isMultipartFileArray(parameter) ||
                (servletPartClass != null && (servletPartClass == paramType ||
                        isPartCollection(parameter) || isPartArray(parameter))));
    }

    public static Object resolveMultipartArgument(String name, MethodParameter parameter, HttpServletRequest request)
            throws Exception {

        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        boolean isMultipart = (multipartRequest != null || isMultipartContent(request));

        if (MultipartFile.class == parameter.getNestedParameterType()) {
            if (multipartRequest == null && isMultipart) {
                multipartRequest = adaptToMultipartHttpServletRequest(request);
            }
            return (multipartRequest != null ? multipartRequest.getFile(name) : null);
        }
        else if (isMultipartFileCollection(parameter)) {
            if (multipartRequest == null && isMultipart) {
                multipartRequest = adaptToMultipartHttpServletRequest(request);
            }
            return (multipartRequest != null ? multipartRequest.getFiles(name) : null);
        }
        else if (isMultipartFileArray(parameter)) {
            if (multipartRequest == null && isMultipart) {
                multipartRequest = adaptToMultipartHttpServletRequest(request);
            }
            if (multipartRequest != null) {
                List<MultipartFile> multipartFiles = multipartRequest.getFiles(name);
                return multipartFiles.toArray(new MultipartFile[multipartFiles.size()]);
            }
            else {
                return null;
            }
        }
        else if (servletPartClass != null) {
            if (servletPartClass == parameter.getNestedParameterType()) {
                return (isMultipart ? RequestPartResolver.resolvePart(request, name) : null);
            }
            else if (isPartCollection(parameter)) {
                return (isMultipart ? RequestPartResolver.resolvePartList(request, name) : null);
            }
            else if (isPartArray(parameter)) {
                return (isMultipart ? RequestPartResolver.resolvePartArray(request, name) : null);
            }
        }
        return UNRESOLVABLE;
    }

    private static boolean isMultipartFileCollection(MethodParameter methodParam) {
        return (MultipartFile.class == getCollectionParameterType(methodParam));
    }

    private static boolean isMultipartFileArray(MethodParameter methodParam) {
        return (MultipartFile.class == methodParam.getNestedParameterType().getComponentType());
    }

    private static boolean isPartCollection(MethodParameter methodParam) {
        return (servletPartClass == getCollectionParameterType(methodParam));
    }

    private static boolean isPartArray(MethodParameter methodParam) {
        return (servletPartClass == methodParam.getNestedParameterType().getComponentType());
    }

    private static Class<?> getCollectionParameterType(MethodParameter methodParam) {
        Class<?> paramType = methodParam.getNestedParameterType();
        if (Collection.class == paramType || List.class.isAssignableFrom(paramType)){
            Class<?> valueType = ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric();
            if (valueType != null) {
                return valueType;
            }
        }
        return null;
    }


    /**
     * Inner class to avoid hard-coded dependency on Servlet 3.0 Part type...
     */
    private static class RequestPartResolver {

        public static Object resolvePart(HttpServletRequest servletRequest, String name) throws Exception {
            return servletRequest.getPart(name);
        }

        public static Object resolvePartList(HttpServletRequest servletRequest, String name) throws Exception {
            Collection<Part> parts = servletRequest.getParts();
            List<Part> result = new ArrayList<Part>(parts.size());
            for (Part part : parts) {
                if (part.getName().equals(name)) {
                    result.add(part);
                }
            }
            return result;
        }

        public static Object resolvePartArray(HttpServletRequest servletRequest, String name) throws Exception {
            Collection<Part> parts = servletRequest.getParts();
            List<Part> result = new ArrayList<Part>(parts.size());
            for (Part part : parts) {
                if (part.getName().equals(name)) {
                    result.add(part);
                }
            }
            return result.toArray(new Part[result.size()]);
        }
    }

}

