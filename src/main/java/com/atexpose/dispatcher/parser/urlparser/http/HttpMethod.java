package com.atexpose.dispatcher.parser.urlparser.http;

import com.atexpose.errors.RuntimeError;

/**
 * The purpose enum is to encapsulate the request methods.
 * <p>
 * Created by Schinzel on 2017-04-13.
 */
enum HttpMethod {
    GET("GET"),
    POST("POST");
    final String mRequestMethodAsString;


    /**
     * @param requestMethodAsString The name of the request method
     */
    HttpMethod(String requestMethodAsString) {
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
     * @return The http method of the argument http request request
     */
    static HttpMethod getRequestMethod(String httpRequest) {
        if (GET.isThisTypeOfRequest(httpRequest)) {
            return GET;
        } else if (POST.isThisTypeOfRequest(httpRequest)) {
            return POST;
        } else {
            throw new RuntimeError("Request not allowed. Request has to start with GET or POST. Request:' " + httpRequest + "'");
        }
    }
}
