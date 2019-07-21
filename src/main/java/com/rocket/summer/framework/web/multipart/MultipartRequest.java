package com.rocket.summer.framework.web.multipart;

import com.rocket.summer.framework.util.MultiValueMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This interface defines the multipart request access operations
 * that are exposed for actual multipart requests. It is extended
 * by {@link MultipartHttpServletRequest}
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @since 2.5.2
 */
public interface MultipartRequest {

    /**
     * Return an {@link java.util.Iterator} of String objects containing the
     * parameter names of the multipart files contained in this request. These
     * are the field names of the form (like with normal parameters), not the
     * original file names.
     * @return the names of the files
     */
    Iterator<String> getFileNames();

    /**
     * Return the contents plus description of an uploaded file in this request,
     * or <code>null</code> if it does not exist.
     * @param name a String specifying the parameter name of the multipart file
     * @return the uploaded content in the form of a {@link MultipartFile} object
     */
    MultipartFile getFile(String name);

    /**
     * Return the contents plus description of uploaded files in this request,
     * or an empty list if it does not exist.
     * @param name a String specifying the parameter name of the multipart file
     * @return the uploaded content in the form of a {@link MultipartFile} list
     * @since 3.0
     */
    List<MultipartFile> getFiles(String name);

    /**
     * Return a {@link java.util.Map} of the multipart files contained in this request.
     * @return a map containing the parameter names as keys, and the
     * {@link MultipartFile} objects as values
     */
    Map<String, MultipartFile> getFileMap();

    /**
     * Return a {@link MultiValueMap} of the multipart files contained in this request.
     * @return a map containing the parameter names as keys, and a list of
     * {@link MultipartFile} objects as values
     * @since 3.0
     */
    MultiValueMap<String, MultipartFile> getMultiFileMap();

}

