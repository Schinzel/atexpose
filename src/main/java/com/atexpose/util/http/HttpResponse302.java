package com.atexpose.util.http;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponse302 {
    @Getter
    private final String response;


    @Builder
    HttpResponse302(String location, Map<String, String> customResponseHeaders) {
        if (customResponseHeaders == null) {
            customResponseHeaders = new HashMap<>();
        }
        customResponseHeaders.put("location", location);
        response = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.REDIRECT)
                .customResponseHeaders(customResponseHeaders)
                .contentType(ContentType.TEXT)
                .build()
                .getHeader()
                .getString();
    }


    public static String wrap(String newLocation) {
        return HttpResponse302.builder()
                .location(newLocation)
                .build()
                .getResponse();
    }
}
