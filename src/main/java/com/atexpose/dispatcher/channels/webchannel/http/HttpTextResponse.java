package com.atexpose.dispatcher.channels.webchannel.http;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.atexpose.util.EncodingUtil;
import io.schinzel.basicutils.str.Str;

import static io.schinzel.basicutils.str.Str.WS;

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
                .a("HTTP/1.1 200 OK").aws(WS.CR_LF)
                .a("Server: ").a(PropertiesDispatcher.RESP_HEADER_SERVER_NAME).aws(WS.CR_LF)
                .a("Content-Length: ").a(returnLength).aws(WS.CR_LF)
                .a("Content-Type: text/html; charset=UTF-8").aws(WS.CR_LF)
                .a("Cache-Control: max-age=0").aws(WS.CR_LF)
                .aws(WS.CR_LF)
                .a(message)
                .toString();
    }
}
