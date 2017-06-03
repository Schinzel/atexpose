package com.atexpose.util.httpresponse;

import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * The purpose of this file is to compile a internal server error response.
 *
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponse500 {
    @Getter
    private final String response;

    @Builder
    HttpResponse500(String body, Map<String, String> customResponseHeaders) {
        int contentLength = UTF8.getBytes(body).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .customResponseHeaders(customResponseHeaders)
                .contentType(ContentType.TEXT)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(body)
                .getString();
    }
}
