package com.atexpose.util.http_response;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.atexpose.dispatcher.channels.web_channel.WebCookie;
import com.google.common.base.Joiner;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.str.Str;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The purpose of this class is to represent an http header.
 * <p>
 * Created by Schinzel on 2017-05-31.
 */
class HttpHeader {
    @Getter
    private final Str header;


    @Builder
    HttpHeader(HttpStatusCode httpStatusCode,
               Map<String, String> customHeaders,
               List<WebCookie> cookieList,
               ContentType contentType,
               int browserCacheMaxAgeInSeconds,
               int contentLength) {
        if (customHeaders == null) {
            customHeaders = Collections.emptyMap();
        }
        final String setCookieHeaderLines = Checker.isEmpty(cookieList)
                ? ""
                : cookieList
                .stream()
                .map(WebCookie::getHttpHeaderSetCookieString)
                .collect(Collectors.joining(""));
        header = Str.create()
                .a("HTTP/1.1 ").acrlf(httpStatusCode.getCode())
                .a("Server: ").acrlf(PropertiesDispatcher.RESP_HEADER_SERVER_NAME)
                .a("Content-Type: ").acrlf(contentType.getContentType())
                .a("Cache-Control: ").a("max-age=").acrlf(String.valueOf(browserCacheMaxAgeInSeconds))
                .a("Content-Length: ").acrlf(String.valueOf(contentLength))
                //If there are custom response headers
                .ifTrue(Checker.isNotEmpty(customHeaders))
                //Add the custom response headers
                .acrlf(Joiner.on("\r\n").withKeyValueSeparator(": ").join(customHeaders))
                .endIf()
                .a(setCookieHeaderLines)
                .acrlf();
    }


}
