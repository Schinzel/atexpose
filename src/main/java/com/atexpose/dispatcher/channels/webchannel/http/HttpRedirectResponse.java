package com.atexpose.dispatcher.channels.webchannel.http;

import com.atexpose.dispatcher.PropertiesDispatcher;
import io.schinzel.basicutils.str.Str;

import java.net.URI;

import static io.schinzel.basicutils.str.Str.WS;

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
        return Str.create().a("HTTP/1.1 302").aws(WS.CR_LF)
                .a("Server: ").a(PropertiesDispatcher.RESP_HEADER_SERVER_NAME).aws(WS.CR_LF)
                .a("Content-Length: ").a("0").aws(WS.CR_LF)
                .a("Location: ").a(uri.toString()).aws(WS.CR_LF)
                .aws(WS.CR_LF)
                .toString();
    }
}
