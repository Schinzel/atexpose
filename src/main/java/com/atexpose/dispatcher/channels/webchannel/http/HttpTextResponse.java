package com.atexpose.dispatcher.channels.webchannel.http;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.atexpose.util.EncodingUtil;
import io.schinzel.basicutils.str.Str;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-04-13.
 */
public class HttpTextResponse {


    /**
     * @return The argument message as a http response with header.
     */
    public static String wrap(String message) {
        int returnLength = EncodingUtil.convertToByteArray(message).length;
        return Str.create()
                .acrlf("HTTP/1.1 200 OK")
                .a("Server: ").acrlf(PropertiesDispatcher.RESP_HEADER_SERVER_NAME)
                .a("Content-Length: ").acrlf(String.valueOf(returnLength))
                .acrlf("Content-Type: text/html; charset=UTF-8")
                .acrlf("Cache-Control: max-age=0")
                .acrlf()
                .a(message)
                .toString();
    }
}
