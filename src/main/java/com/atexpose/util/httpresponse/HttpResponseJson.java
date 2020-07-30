package com.atexpose.util.httpresponse;

import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

/**
 * The purpose of this file is to compile a http json response.
 * <p>
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponseJson {
    @Getter
    private final String response;


    @Builder
    HttpResponseJson(@NonNull String body, Map<String, String> customHeaders) {
        int contentLength = UTF8.getBytes(body).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .customHeaders(customHeaders)
                .contentType(ContentType.JSON)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(body)
                //The two extra new-lines needs to be there for Safari to be able to parse the JSON.
                .a("\n\n")
                .asString();
    }
}
