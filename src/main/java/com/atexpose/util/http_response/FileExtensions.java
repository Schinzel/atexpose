package com.atexpose.util.http_response;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.thrower.Thrower;
import org.apache.commons.io.FilenameUtils;

import java.util.Map;

/**
 * Mapping between file extensions and http header content types.
 * <p>
 * Created by schinzel on 2017-06-01.
 */
class FileExtensions {
    private static final Map<String, ContentType> CONTENT_TYPES = ImmutableMap.<String, ContentType>builder()
            //Images
            .put("ico", ContentType.ICO)
            .put("png", ContentType.PNG)
            .put("jpg", ContentType.JPG)
            .put("jpeg", ContentType.JPG)
            .put("gif", ContentType.GIF)
            .put("svg", ContentType.SVG)
            //Text files
            .put("html", ContentType.HTML)
            .put("htm", ContentType.HTML)
            .put("css", ContentType.CSS)
            .put("js", ContentType.JAVASCRIPT)
            .put("txt", ContentType.TEXT_FILE)
            .put("json", ContentType.JSON)
            //Other
            .put("map", ContentType.MAP)
            .put("pdf", ContentType.PDF)
            .put("woff", ContentType.WOFF)
            .put("woff2", ContentType.WOFF2)
            .build();


    /**
     * @param filename The filename to check.
     * @return The content type for the argument filename
     */
    static ContentType getContentType(String filename) {
        String filenameExtension = FilenameUtils.getExtension(filename);
        Thrower.throwIfFalse(CONTENT_TYPES.containsKey(filenameExtension), "No know content type for file '" + filename + "'");
        return CONTENT_TYPES.get(filenameExtension);
    }
}
