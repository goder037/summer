package com.rocket.summer.framework.core.io;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

import java.io.File;

public class FileSystemResource {

    private final File file;

    private final String path;

    /**
     * Create a new FileSystemResource from a File handle.
     * <p>Note: When building relative resources via {@link #createRelative},
     * the relative path will apply <i>at the same directory level</i>:
     * e.g. new File("C:/dir1"), relative path "dir2" -> "C:/dir2"!
     * If you prefer to have relative paths built underneath the given root
     * directory, use the {@link #FileSystemResource(String) constructor with a file path}
     * to append a trailing slash to the root path: "C:/dir1/", which
     * indicates this directory as root for all relative paths.
     * @param file a File handle
     */
    public FileSystemResource(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
        this.path = StringUtils.cleanPath(file.getPath());
    }
}
