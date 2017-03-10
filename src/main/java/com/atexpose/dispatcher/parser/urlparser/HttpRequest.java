package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.errors.RuntimeError;
import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.Map;

import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;

/**
 * The purpose of this class is to handle HttpRequests. Requests methods POST
 * and GET are supported. The class handles the different type of request
 * methods transparently.
 *
 * @author schinzel
 */
public class HttpRequest {
    /**
     * Marks the end of the url in http request.
     */
    private static final String END_OF_URL = " HTTP/1.1";
    /**
     * Marks the delimiter between header and body.
     */
    private static final String HEADER_BODY_DELIMITER = "\r\n\r\n";
    /**
     * The delimiter between header fields.
     */
    private static final String HEADER_FIELD_DELIMITER = "\r\n";
    /**
     * Holds the request method used.
     */
    private final RequestMethod mRequestMethod;
    /**
     * Holds the http request.
     */
    private final String mHttpRequest;

    /**
     * The purpose enum is to encapsualte the request methods.
     */
    private enum RequestMethod {
        GET("GET"),
        POST("POST");
        private final String mRequestMethodAsString;


        /**
         * @param requestMethodAsString The name of the request method
         */
        RequestMethod(String requestMethodAsString) {
            mRequestMethodAsString = requestMethodAsString + " /";
        }


        /**
         * @param httpRequest A whole http requst with header and everything
         * @return returns true if this argument http request was of this type
         */
        private boolean isThisTypeOfRequest(String httpRequest) {
            return (httpRequest.indexOf(mRequestMethodAsString) == 0);
        }


        /**
         * @param httpRequest A whole http request
         * @return Which enum the argument http request was
         */
        static RequestMethod getRequestMethod(String httpRequest) {
            if (GET.isThisTypeOfRequest(httpRequest)) {
                return GET;
            } else if (POST.isThisTypeOfRequest(httpRequest)) {
                return POST;
            } else {
                throw new RuntimeError("Request not allowed. Request has to start with GET or POST. Request:' " + httpRequest + "'");
            }
        }
    }


    /**
     * @param httpRequest A whole http request
     */
    public HttpRequest(String httpRequest) {
        mRequestMethod = RequestMethod.getRequestMethod(httpRequest);
        mHttpRequest = httpRequest;
    }


    /**
     * @return The url part of a request. Including the query string variables
     * if any.
     */
    String getURL() {
        int start = mHttpRequest.indexOf(mRequestMethod.mRequestMethodAsString);
        if (start == -1) {
            //throw error
            throw new RuntimeError("Request method marker '" + mRequestMethod.mRequestMethodAsString + "' was missing");
        }
        //Set the start to be where the request label was found plus the length of the request label
        start += mRequestMethod.mRequestMethodAsString.length();
        //Set the end to be end of url
        int end = mHttpRequest.indexOf(END_OF_URL);
        //If the end of url marker was not found
        if (end == -1) {
            throw new RuntimeError("HTTP protocal marker '" + END_OF_URL + "' was missing");
        }
        //Get the substring
        return mHttpRequest.substring(start, end);
    }


    /**
     * @return The method name invoked
     */
    String getPath() {
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
        if (mRequestMethod == RequestMethod.GET) {
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
    Map<String, String> getVariablesAsMap() {
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


    Map<String, String> getCookies() {
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
