package com.atexpose.dispatcher.parser.urlparser.httprequest;

import com.google.common.base.Splitter;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.substring.SubString;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
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
    /** Holds the request method used. */
    private final HttpMethod mHttpMethod;
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
        mHttpMethod = this.isGhostCall()
                ? HttpMethod.GET
                : HttpMethod.getRequestMethod(httpRequest);
        mBody = SubString.create(httpRequest).startDelimiter(HEADER_BODY_DELIMITER).getString();
        mURL = SubString.create(httpRequest)
                .startDelimiter(mHttpMethod.getAsString())
                .endDelimiter(END_OF_URL)
                .getString();
        mPath = SubString.create(mURL).endDelimiter("?").getString();
        String queryString = SubString.create(mURL).startDelimiter("?").getString();
        String variablesAsString = (mHttpMethod == HttpMethod.GET)
                ? queryString
                : mBody;
        mVariables = Checker.isEmpty(variablesAsString)
                ? Collections.emptyMap()
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
        String headerValue = "";
        //If argument header name was empty
        if (Checker.isEmpty(headerName)) {
            return headerValue;
        }
        //Get the end of the request header
        int endOfRequestHeader = mHttpRequest.indexOf(HEADER_BODY_DELIMITER);
        //Add a colon to header name
        headerName = headerName + ":";
        //Get the start pos of the header name
        int startPosHeaderName = StringUtils.indexOfIgnoreCase(mHttpRequest, headerName);
        //If the header name was found and it was before the end of the header
        if (startPosHeaderName > -1 && startPosHeaderName < endOfRequestHeader) {
            //Get the end of the line after the header name
            int endOfHeaderField = mHttpRequest.indexOf(HEADER_FIELD_DELIMITER, startPosHeaderName);
            //If a end of header was found 
            if (endOfHeaderField > -1) {
                //Get the value, which is between the colon and the end of the line
                headerValue = mHttpRequest.substring(startPosHeaderName + headerName.length(), endOfHeaderField);
                headerValue = headerValue.trim();
            }
        }
        return headerValue;
    }


    public Map<String, String> getCookies() {
        Map<String, String> cookies = new HashMap<>();
        String cookieString = getHeaderValue("Cookie");
        if (Checker.isEmpty(cookieString)) {
            return cookies;
        }
        String[] cookieValues = cookieString.split(";");
        for (String cookieValue : cookieValues) {
            String[] aCookie = cookieValue.split("=");
            if (2 == aCookie.length) {
                cookies.put(aCookie[0].trim(), aCookie[1].trim());
            }
        }
        return cookies;
    }

}
