package com.atexpose.util.httpresponse;

import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;

import java.util.Map;

/**
 * The purpose of this file is to compile a internal server error response.
 * <p>
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponse500 {
    @Getter
    private final String response;


    @Builder
    HttpResponse500(@NonNull JSONObject body, Map<String, String> customHeaders) {
        String bodyAsString = body.toString();
        int contentLength = UTF8.getBytes(bodyAsString).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .customHeaders(customHeaders)
                .contentType(ContentType.JSON)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(bodyAsString)
                //The two extra new-lines needs to be there for Safari to be able to parse the JSON.
                .a("\n\n")
                .getString();
    }
}
