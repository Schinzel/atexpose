package com.atexpose.dispatcher.channels.webchannel.http;

import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import com.atexpose.dispatcher.parser.urlparser.RedirectHttpStatus;
import io.schinzel.basicutils.Checker;

/**
 * The purpose of this class is to handle redirects from http to https.
 *
 * Created by schinzel on 2017-04-10.
 */
public class HttpsRedirect {
    /** The name of the server as written in the response header **/
    private static final String RESPONSE_HEADER_LINE_BREAK = "\r\n";
    private static final String SERVER_NAME = "AtExpose";


    public static boolean isHttpReuqest(HttpRequest httpRequest) {
        //Get the Heroku router protocol header
        String herokuProtoHeader = httpRequest.getRequestHeaderValue("X-Forwarded-Proto");
        //If there was a Heroku's router header and this header was "http"
        if (!Checker.isEmpty(herokuProtoHeader) && herokuProtoHeader.equalsIgnoreCase("http")) {
            //return true
            return true;
        }
        //For unit test: if no Heroku router header
        //and there was an url, i.e. the request was readable, i.e. the request was not a https request
        if (Checker.isEmpty(herokuProtoHeader) && httpRequest.getURL() != null) {
            return true;
        }
        return false;
    }


    public static String getUrlWithHttps(String host, String url) {
        return "https://" + host + "/" + url;
    }


    /**
     * Make redirect response to new location for file
     *
     * @param url
     * @return A 301 or 302 redirect to the argument url
     */
    public static String wrapRedirect(String url, RedirectHttpStatus redirectStatusCode) {
        return new StringBuilder()
                .append("HTTP/1.1 ").append(redirectStatusCode.getRedirectCode()).append(RESPONSE_HEADER_LINE_BREAK)
                .append("Server: ").append(SERVER_NAME).append(RESPONSE_HEADER_LINE_BREAK)
                .append("Content-Length: ").append("0").append(RESPONSE_HEADER_LINE_BREAK)
                .append("Location: ").append(url).append(RESPONSE_HEADER_LINE_BREAK)
                .append(RESPONSE_HEADER_LINE_BREAK)
                .toString();
    }
}
