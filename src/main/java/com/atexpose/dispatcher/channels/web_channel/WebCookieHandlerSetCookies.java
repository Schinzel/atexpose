package com.atexpose.dispatcher.channels.web_channel;

import com.atexpose.util.web_cookie.WebCookieHandler;

import java.util.Map;

/**
 * The purpose of this class is to be able to access the method to set cookies to send
 * to client and close the request-response without making these methods available
 * outside of @expose
 */
class WebCookieHandlerSetCookies extends WebCookieHandler {
    static void setRequestCookies(Map<String, String> cookies) {
        setRequestCookiesProtected(cookies);
    }

    static void closeRequestResponse() {
        closeRequestResponseInternal();
    }
}