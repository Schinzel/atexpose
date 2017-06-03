package com.atexpose.util.http;

import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponse500 {
    @Getter
    private final String response;

    @Builder
    HttpResponse500(String message, Map<String, String> customResponseHeaders) {
        int contentLength = UTF8.getBytes(message).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .customResponseHeaders(customResponseHeaders)
                .contentType(ContentType.TEXT)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(message)
                .getString();
    }
}
