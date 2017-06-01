package com.atexpose.util.http;

import io.schinzel.basicutils.UTF8;

import java.util.Map;

/**
 * Created by schinzel on 2017-06-01.
 */
public class HttpResponse404 {
    public HttpResponse404(String filenameMissingFile, Map<String, String> customResponseHeaders) {
        String message = "File '" + filenameMissingFile + "' not found";
        int contentLength = UTF8.getBytes(message).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.FILE_NOT_FOUND)
                .customResponseHeaders(customResponseHeaders)
                .browserCacheMaxAgeInSeconds(0)
                .contentType(ContentType.TEXT)
                .contentLength(contentLength)
                .build();

    }
}
