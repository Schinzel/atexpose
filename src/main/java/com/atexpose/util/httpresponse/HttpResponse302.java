package com.atexpose.util.httpresponse;

import io.schinzel.basicutils.Checker;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to compile a 302 redirect.
 * <p>
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponse302 {
    @Getter
    private final String response;


    @Builder
    HttpResponse302(String location, Map<String, String> customHeaders) {
        Map<String,String> locationHeader = new HashMap<>();
        locationHeader.put("Location", location);
        if (Checker.isNotEmpty(customHeaders)){
            locationHeader.putAll(customHeaders);
        }
        response = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.REDIRECT)
                .customHeaders(locationHeader)
                .contentType(ContentType.TEXT)
                .build()
                .getHeader()
                .getString();
    }

}
