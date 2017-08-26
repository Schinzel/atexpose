package com.atexpose.dispatcher.parser.urlparser.httprequest;

import com.google.common.base.Splitter;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.substring.SubString;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * The purpose of this class is to handle HttpRequests. Requests methods POST
 * and GET are supported. The class handles the different type of request
 * methods transparently.
 *
 * @author schinzel
 */
@Accessors(prefix = "m")
public class HttpRequest {
    /** Marks the end of the url in http request. */
    private static final String END_OF_URL = " HTTP/1.1";
    /** Marks the delimiter between header and body. */
    private static final String HEADER_BODY_DELIMITER = "\r\n\r\n";
    /** The delimiter between header fields. */
    private static final String HEADER_FIELD_DELIMITER = "\r\n";
    /** Holds the http request. */
    private final String mHttpRequest;
    /** True if this is a ghost call, i.e. that the whole request is one byte. */
    @Getter private final boolean mGhostCall;
    /** The body of the http request */
    @Getter private final String mBody;
    /** The url part of a request. Including the query string variables */
    @Getter private final String mURL;
    /** The part of the URL before query string */
    @Getter private final String mPath;
    /** The variables of the request. If GET, query string, if POST body. As map. */
    @Getter private final Map<String, String> mVariables;


    /**
     * @param httpRequest A whole http request
     */
    public HttpRequest(String httpRequest) {
        Thrower.throwIfVarNull(httpRequest, "httpRequest");
        mHttpRequest = httpRequest;
        mGhostCall = (httpRequest.length() == 1);
        HttpMethod httpMethod = this.isGhostCall()
                ? HttpMethod.GET
                : HttpMethod.getRequestMethod(httpRequest);
        mBody = SubString.create(httpRequest).startDelimiter(HEADER_BODY_DELIMITER).getString();
        mURL = SubString.create(httpRequest)
                .startDelimiter(httpMethod.getAsString())
                .endDelimiter(END_OF_URL)
                .getString();
        mPath = SubString.create(mURL).endDelimiter("?").getString();
        //Set variable string to
        String variablesAsString = (httpMethod == HttpMethod.GET)
                //If get request, set query string
                ? SubString.create(mURL).startDelimiter("?").getString()
                //else, i.e. is post request, set body
                : mBody;
        //Set variables to
        mVariables = Checker.isEmpty(variablesAsString)
                //If empty variable string, set to empty map
                ? Collections.emptyMap()
                //else, construct variable map
                : Splitter.on('&').trimResults().withKeyValueSeparator('=').split(variablesAsString);
    }


    @SneakyThrows
    public URI getURI() {
        //Get protocol
        String protocol = this.getHeaderValue("X-Forwarded-Proto");
        if (Checker.isEmpty(protocol)) {
            protocol = "http";
        }
        //Get host
        String host = this.getHeaderValue("Host");
        //Get path and query
        String pathAndQuery = SubString.create(mHttpRequest)
                .startDelimiter("GET ")
                .endDelimiter(" HTTP/1.1")
                .getString();
        //Get the path. If there is a "?" in the string
        String path = pathAndQuery.contains("?")
                //get everything before the "?"
                ? SubString.create(pathAndQuery).endDelimiter("?").getString()
                //else, get the whole string
                : pathAndQuery;
        //Get the query string. If there is a "?" in the string
        String query = pathAndQuery.contains("?")
                //Get everything after the "?"
                ? SubString.create(pathAndQuery).startDelimiter("?").getString()
                //else, get empty string
                : null;
        return new URIBuilder()
                .setScheme(protocol)
                .setHost(host)
                .setPath(path)
                .setCustomQuery(query)
                .build();
    }


    /**
     * @param headerName The name of the header to look up.
     * @return The value of the argument header. Empty string is returned if not
     * found.
     */
    public String getHeaderValue(String headerName) {
        Thrower.throwIfVarEmpty(headerName, "headerName");
        return SubString.create(mHttpRequest)
                .startDelimiter(headerName + ": ")
                .endDelimiter(HEADER_FIELD_DELIMITER)
                .getString();
    }


    /**
     * @return Get the cookies
     */
    public Map<String, String> getCookies() {
        String cookieString = getHeaderValue("Cookie");
        return Checker.isEmpty(cookieString)
                ? Collections.emptyMap()
                : Splitter.on("; ").withKeyValueSeparator("=").split(cookieString);
    }

}
