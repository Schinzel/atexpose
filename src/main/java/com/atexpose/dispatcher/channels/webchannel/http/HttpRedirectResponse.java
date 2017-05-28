package com.atexpose.dispatcher.channels.webchannel.http;

import com.atexpose.dispatcher.PropertiesDispatcher;
import io.schinzel.basicutils.str.Str;

import java.net.URI;

/**
 * The purpose of this class is to handle redirects from http to https.
 * <p>
 * Created by schinzel on 2017-04-10.
 */
public class HttpRedirectResponse {


    /**
     * Make redirect response to new location for file
     *
     * @param uri
     * @return A response header to send to the client.
     */
    public static String getHeader(URI uri) {
        return Str.create()
                .acrlf("HTTP/1.1 302")
                .a("Server: ").acrlf(PropertiesDispatcher.RESP_HEADER_SERVER_NAME)
                .a("Content-Length: ").acrlf("0")
                .a("Location: ").acrlf(uri.toString())
                .acrlf()
                .toString();
    }
}
