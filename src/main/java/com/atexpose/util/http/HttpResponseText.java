package com.atexpose.util.http;

import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Created by schinzel on 2017-06-03.
 */
class HttpResponseText {
    @Getter
    private final String response;

    @Builder
    HttpResponseText(HttpStatusCode httpStatusCode, String message, Map<String, String> customResponseHeaders) {
        int contentLength = UTF8.getBytes(message).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(httpStatusCode)
                .customResponseHeaders(customResponseHeaders)
                .contentType(ContentType.TEXT)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(message)
                .getString();
    }
}
