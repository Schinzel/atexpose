package com.atexpose.util.http_response;

import com.atexpose.util.ArrayUtil;
import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * The purpose of this class is to represent a http response for a file.
 * <p>
 * Created by schinzel on 2017-06-03.
 */
public class HttpResponseFile {
    @Getter
    private final byte[] response;


    @Builder
    HttpResponseFile(byte[] body,
                     String filename,
                     Map<String, String> customHeaders) {
        ContentType contentType = FileExtensions.getContentType(filename);
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .customHeaders(customHeaders)
                .contentType(contentType)
                .contentLength(body.length)
                .build()
                .getHeader()
                .asString();
        response = ArrayUtil.concat(UTF8.getBytes(header), body);
    }

}
