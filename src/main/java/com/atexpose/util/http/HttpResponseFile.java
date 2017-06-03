package com.atexpose.util.http;

import com.atexpose.util.ArrayUtil;
import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponseFile {
    @Getter
    private final byte[] response;


    @Builder
    HttpResponseFile(byte[] body, String fileName, Map<String, String> customResponseHeaders) {
        ContentType contentType = FileExtensions.getContentType(fileName);
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .customResponseHeaders(customResponseHeaders)
                .contentType(contentType)
                .contentLength(body.length)
                .build()
                .getHeader()
                .getString();
        response = ArrayUtil.concat(UTF8.getBytes(header), body);
    }

}
