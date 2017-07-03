package com.atexpose.util.httpresponse;

import com.google.common.base.Charsets;
import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

/**
 * The purpose of this file is to compile a 404 http response.
 * <p>
 * Created by schinzel on 2017-06-01.
 */
public class HttpResponse404 {
    @Getter
    private final byte[] response;


    @Builder
    HttpResponse404(@NonNull String filenameMissingFile, Map<String, String> customHeaders) {
        String body = "File '" + filenameMissingFile + "' not found";
        int contentLength = UTF8.getBytes(body).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.FILE_NOT_FOUND)
                .customHeaders(customHeaders)
                .contentType(ContentType.TEXT)
                .contentLength(contentLength)
                .build();
        response = header.getHeader()
                .a(body)
                .getString()
                .getBytes(Charsets.UTF_8);
    }
}