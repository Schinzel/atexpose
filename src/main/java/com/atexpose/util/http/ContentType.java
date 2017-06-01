package com.atexpose.util.http;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.Thrower;
import org.apache.commons.io.FilenameUtils;

import java.util.Map;

/**
 * The purpose of this to find a content type given a filename.
 * <p>
 * Created by schinzel on 2017-05-30.
 */
class ContentType {
    private static final Map<String, String> CONTENT_TYPES = ImmutableMap.<String, String>builder()
            //Images
            .put("ico", "image/ico")
            .put("png", "image/png")
            .put("jpg", "image/jpg")
            .put("jpeg", "image/jpeg")
            .put("gif", "image/gif")
            .put("svg", "image/svg+xml")
            //Text files
            .put("html", "text/html")
            .put("htm", "text/html")
            .put("css", "text/css")
            .put("js", "text/javascript")
            .put("txt", "text/plain")
            .put("json", "application/json")
            //Other
            .put("map", "text/plain")
            .put("pdf", "application/pdf")
            .put("woff", "application/font-woff")
            .put("woff2", "application/font-woff2")
            .build();

    private ContentType(){}

    static String get(String filename) {
        String filenameExtension = FilenameUtils.getExtension(filename);
        Thrower.throwIfFalse(CONTENT_TYPES.containsKey(filenameExtension), "No know content type for file '" + filename + "'");
        return CONTENT_TYPES.get(filenameExtension);
    }
}
