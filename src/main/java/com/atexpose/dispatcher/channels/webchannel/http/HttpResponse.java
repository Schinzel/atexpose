package com.atexpose.dispatcher.channels.webchannel.http;

import com.atexpose.util.EncodingUtil;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-04-13.
 */
public class HttpResponse {
    private static final String RESPONSE_HEADER_LINE_BREAK = "\r\n";
    private static final String SERVER_NAME = "AtExpose";


    /**
     * @return The argument message as a http response with header.
     */
    public static String wrap(String message) {
        int returnLength = EncodingUtil.convertToByteArray(message).length;
        return new StringBuilder()
                .append("HTTP/1.1 200 OK").append(RESPONSE_HEADER_LINE_BREAK)
                .append("Server: ").append(SERVER_NAME).append(RESPONSE_HEADER_LINE_BREAK)
                .append("Content-Length: ").append(returnLength).append(RESPONSE_HEADER_LINE_BREAK)
                .append("Content-Type: ").append("text/html; charset=UTF-8").append(RESPONSE_HEADER_LINE_BREAK)
                .append("Cache-Control: ").append("max-age=0").append(RESPONSE_HEADER_LINE_BREAK)
                .append(RESPONSE_HEADER_LINE_BREAK)
                .append(message)
                .toString();
    }
}
