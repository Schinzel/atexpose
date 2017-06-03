package com.atexpose.util.httpresponse;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to represent a 302 redirect.
 * <p>
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

}
