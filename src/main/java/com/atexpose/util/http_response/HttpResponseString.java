package com.atexpose.util.http_response;

import com.atexpose.util.web_cookie.ResponseCookie;
import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * The purpose of this file is to compile a http string response.
 * <p>
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponseString {
    @Getter
    private final String response;


    @Builder
    HttpResponseString(
            @NonNull String body,
            Map<String, String> customHeaders,
            List<ResponseCookie> cookieList) {
        int contentLength = UTF8.getBytes(body).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .customHeaders(customHeaders)
                .cookieList(cookieList)
                .contentType(ContentType.TEXT)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(body)
                .asString();
    }


}
