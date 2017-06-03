package com.atexpose.util.http;

import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Created by schinzel on 2017-06-01.
 */
public class HttpResponse404 {
    @Getter
    private final String response;

    @Builder
    HttpResponse404(String filenameMissingFile, Map<String, String> customResponseHeaders) {
        String body = "File '" + filenameMissingFile + "' not found";
        int contentLength = UTF8.getBytes(body).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.FILE_NOT_FOUND)
                .customResponseHeaders(customResponseHeaders)
                .contentType(ContentType.TEXT)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(body)
                .getString();
    }
}
