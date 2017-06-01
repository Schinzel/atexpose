package com.atexpose.util.http;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.google.common.base.Joiner;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.str.Str;
import lombok.Getter;

import java.util.Map;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-05-31.
 */
public class HttpHeader {
    @Getter
    private final Str header;

    HttpHeader(HttpStatusCode httpStatusCode, Map<String, String> customResponseHeaders, int browserCacheMaxAgeInSeconds, String contentType, int contentLength) {
        header = Str.create()
                .a("HTTP/1.1 ").acrlf(httpStatusCode.getCode())
                .a("Server: ").acrlf(PropertiesDispatcher.RESP_HEADER_SERVER_NAME)
                .a("Content-Type: ").acrlf(contentType)
                .a("Cache-Control: ").a("max-age=").acrlf(String.valueOf(browserCacheMaxAgeInSeconds))
                .a("Content-Length: ").acrlf(String.valueOf(contentLength));
        //If there are any response headers to attach
        if (!Checker.isEmpty(customResponseHeaders)) {
            //Add the response headers
            header.acrlf(Joiner.on("\r\n").withKeyValueSeparator(": ").join(customResponseHeaders));
        }
        header.acrlf();
    }


}
