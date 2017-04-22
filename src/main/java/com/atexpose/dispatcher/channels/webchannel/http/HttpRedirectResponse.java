package com.atexpose.dispatcher.channels.webchannel.http;

import io.schinzel.basicutils.str.Str;
import java.net.URI;
import static io.schinzel.basicutils.str.Str.WS;

/**
 * The purpose of this class is to handle redirects from http to https.
 * <p>
 * Created by schinzel on 2017-04-10.
 */
public class HttpRedirectResponse {
    private static final String SERVER_NAME = "@Expose";


    /**
     * Make redirect response to new location for file
     *
     * @param uri
     * @return A response header to send to the client.
     */
    public static String getHeader(URI uri) {
        return Str.create().a("HTTP/1.1 302").aws(WS.CR_LF)
                .a("Server: ").a(SERVER_NAME).aws(WS.CR_LF)
                .a("Content-Length: ").a("0").aws(WS.CR_LF)
                .a("Location: ").a(uri.toString()).aws(WS.CR_LF)
                .aws(WS.CR_LF)
                .toString();
    }
}
