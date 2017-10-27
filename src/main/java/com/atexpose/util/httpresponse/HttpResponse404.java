package com.atexpose.util.httpresponse;

import com.google.common.base.Charsets;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * The purpose of this file is to compile a 404 http response.
 * <p>
 * Created by schinzel on 2017-06-01.
 */
@Accessors(prefix = "m")
public class HttpResponse404 {
    @Getter
    private final byte[] mResponse;
    private String mHtml;


    @Builder
    HttpResponse404(@NonNull String filenameMissingFile, Map<String, String> customHeaders, String html) {
        //If no 404-page was supplied
        if (Checker.isEmpty(html)) {
            //Set 404-page to be default 404-page
            html = "<html><body><center>File '" + filenameMissingFile + "' not found</center><body></html>";
        }
        int contentLength = UTF8.getBytes(html).length;
        HttpHeader header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.FILE_NOT_FOUND)
                .customHeaders(customHeaders)
                .contentType(ContentType.HTML)
                .contentLength(contentLength)
                .build();
        mResponse = header.getHeader()
                .a(html)
                .getString()
                .getBytes(Charsets.UTF_8);
    }
}
