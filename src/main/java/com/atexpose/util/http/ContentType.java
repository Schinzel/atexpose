package com.atexpose.util.http;

import io.schinzel.basicutils.Thrower;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

public enum ContentType {
    //Images
    ICO("image/ico", "ico"),
    PNG("image/png", "png"),
    JPG("image/jpg", "jpg", "jpeg"),
    GIF("image/gif", "gif"),
    SVG("image/svg+xml", "svg"),
    //Text files
    HTML("text/html", "html", "htm"),
    CSS("text/css", "css"),
    JAVASCRIPT("text/javascript", "js"),
    TEXT_FILE("text/plain", "txt"),
    JSON("application/json", "json"),
    //Other
    MAP("text/plain", "map"),
    PDF("application/pdf", "pdf"),
    WOFF("application/font-woff", "woff"),
    WOFF2("application/font-woff2", "woff2");

    @Getter private final String contentType;
    private final String[] extensions;

    static class Holder {
        static Map<String, ContentType> MAP = new HashMap<>();
    }


    ContentType(String contType, String... extensions) {
        this.contentType = contType;
        this.extensions = extensions;
        for (String extension : extensions) {
            Holder.MAP.put(extension, this);
        }
    }


    public static ContentType getContentType(String filename) {
        String filenameExtension = FilenameUtils.getExtension(filename);
        ContentType contentType2 = Holder.MAP.get(filenameExtension);
        Thrower.throwIfVarNull(contentType2, "No content type for file named '" + filename + "'");
        return contentType2;
    }
}
