package com.atexpose.dispatcher.parser.urlparser.httprequest;

import com.atexpose.errors.RuntimeError;

/**
 * The purpose enum is to encapsulate the request methods.
 * <p>
 * Created by Schinzel on 2017-04-13.
 */
enum HttpMethod {
    GET, POST;


    /**
     * @return The name of this request method as string followed by a slash. E.g. "GET/"
     */
    public String getAsString() {
        return this.name() + " /";
    }


    /**
     * @param httpRequest A whole http request
     * @return The http method of the argument http request request
     */
    static HttpMethod getRequestMethod(String httpRequest) {
        if (httpRequest.startsWith(GET.getAsString())) {
            return GET;
        } else if (httpRequest.startsWith(POST.getAsString())) {
            return POST;
        } else {
            throw new RuntimeError("Request not allowed. Request has to start with GET or POST. Request: ' " + httpRequest + "'");
        }
    }
}
