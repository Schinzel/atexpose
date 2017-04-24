package com.atexpose.dispatcher.parser.urlparser.httprequest;

import com.atexpose.errors.RuntimeError;
import com.google.common.base.Splitter;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.substringer.SubStringer;
import io.schinzel.basicutils.Thrower;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
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


    /**
     * @param httpRequest A whole http request
     */
    public HttpRequest(String httpRequest) {
        Thrower.throwIfNull(httpRequest, "httpRequest");
        mHttpRequest = httpRequest;
        mGhostCall = (httpRequest.length() == 1);
        mHttpMethod = this.isGhostCall()
                ? HttpMethod.GET
                : HttpMethod.getRequestMethod(httpRequest);
    }


    /**
     * @return The url part of a request. Including the query string variables
     * if any.
     */
    public String getURL() {
        int start = mHttpRequest.indexOf(mHttpMethod.mRequestMethodAsString);
        if (start == -1) {
            //throw error
            throw new RuntimeError("Request method marker '" + mHttpMethod.mRequestMethodAsString + "' was missing");
        }
        //Set the start to be where the request label was found plus the length of the request label
        start += mHttpMethod.mRequestMethodAsString.length();
        //Set the end to be end of url
        int end = mHttpRequest.indexOf(END_OF_URL);
        //If the end of url marker was not found
        if (end == -1) {
            throw new RuntimeError("HTTP protocal marker '" + END_OF_URL + "' was missing");
        }
        //Get the substring
        return mHttpRequest.substring(start, end);
    }


    @SneakyThrows
    public URI getURI() {
        //Get protocol
        String protocol = this.getRequestHeaderValue("X-Forwarded-Proto");
        if (Checker.isEmpty(protocol)) {
            protocol = "http";
        }
        //Get host
        String host = this.getRequestHeaderValue("Host");
        //Get path and query
        SubStringer pathAndQuery = SubStringer.create(mHttpRequest)
                .startDelimiter("GET ")
                .endDelimiter(" HTTP/1.1")
                .getSubStringer();
        //Get the path. If there is a "?" in the string
        String path = pathAndQuery.contains("?")
                //get everything before the "?"
                ? pathAndQuery.endDelimiter("?").toString()
                //else, get the whole string
                : pathAndQuery.toString();
        //Get the querystring. If there is a "?" in the string
        String query = pathAndQuery.contains("?")
                //Get everything after the "?"
                ? pathAndQuery.startDelimiter("?").toString()
                //else, get empty string
                : EmptyObjects.EMPTY_STRING;
        return new URIBuilder()
                .setScheme(protocol)
                .setHost(host)
                .setPath(path)
                .setQuery(query)
                .build();
    }


    /**
     * @return The method name invoked
     */
    public String getPath() {
        String url = this.getURL();
        int end = url.indexOf('?');
        //If there was no question mark
        if (end == -1) {
            //Set end to be length of url
            end = url.length();
        }
        return url.substring(0, end);
    }


    /**
     * @return The variable of the request. The body if this is a POST the
     * querystring of this is a GET.
     */
    String getVariablesAsString() {
        //If this is a GET method
        if (mHttpMethod == HttpMethod.GET) {
            //Get and return the querystring
            return this.getQueryString();
        }//else, i.e. this is a POST method
        else {
            //Get and return the body
            return this.getBody();
        }
    }


    /**
     * @return The variable of the request. The body if this is a POST the
     * querystring of this is a GET.
     */
    public Map<String, String> getVariablesAsMap() {
        Map<String, String> map = EmptyObjects.EMPTY_MAP;
        String variablesAsString = this.getVariablesAsString();
        if (!Checker.isEmpty(variablesAsString)) {
            map = Splitter.on('&').trimResults().withKeyValueSeparator('=').split(variablesAsString);
        }
        return map;
    }


    /**
     * @return The querystring part of the URL.
     */
    String getQueryString() {
        String url = this.getURL();
        String returnString = EmptyObjects.EMPTY_STRING;
        int start = url.indexOf('?');
        //If there was a question mark
        if (start != -1) {
            returnString = url.substring(start + 1);
        }
        return returnString;
    }


    /**
     * @return The body of the reqeust
     */
    public String getBody() {
        String returnString = EmptyObjects.EMPTY_STRING;
        //Get the string after the double line break
        int start = mHttpRequest.indexOf(HEADER_BODY_DELIMITER);
        //If the delimiter was  found
        if (start != -1) {
            start += HEADER_BODY_DELIMITER.length();
            int end = mHttpRequest.length();
            returnString = mHttpRequest.substring(start, end);
        }
        return returnString;
    }


    /**
     * @param headerName The name of the header to look up.
     * @return The value of the argument header. Empty string is returned if not
     * found.
     */
    public String getRequestHeaderValue(String headerName) {
        String headerValue = EmptyObjects.EMPTY_STRING;
        //If argument header name was empty
        if (Checker.isEmpty(headerName)) {
            return headerValue;
        }
        //Get the end of the request header
        int endOfRequestHeader = mHttpRequest.indexOf(HEADER_BODY_DELIMITER);
        //Add a colon to header name
        headerName = headerName + ":";
        //Get the start pos of the header name
        int startPosHeaderName = mHttpRequest.indexOf(headerName);
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
        String cookieString = getRequestHeaderValue("Cookie");
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
