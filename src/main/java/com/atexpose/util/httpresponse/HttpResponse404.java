package com.atexpose.util.httpresponse;

import com.atexpose.util.ArrayUtil;
import com.google.common.base.Charsets;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.UTF8;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * The purpose of this file is to compile a 404 http response.
 * <p>
 * Created by schinzel on 2017-06-01.
 */
@Accessors(prefix = "m")
public class HttpResponse404 {
    private static final byte[] DEFAULT_404 = "<html><body><center>File not found</center><body></html>"
            .getBytes(Charsets.UTF_8);
    @Getter
    private final byte[] mResponse;


    @Builder
    HttpResponse404(Map<String, String> customHeaders, byte[] body) {
        //If no 404-page was supplied
        if (Checker.isEmpty(body)) {
            //Set 404-page to be default 404-page
            body = DEFAULT_404;
        }
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.FILE_NOT_FOUND)
                .customHeaders(customHeaders)
                .contentType(ContentType.HTML)
                .contentLength(body.length)
                .build()
                .getHeader()
                .getString();
        mResponse = ArrayUtil.concat(UTF8.getBytes(header), body);
    }
}
