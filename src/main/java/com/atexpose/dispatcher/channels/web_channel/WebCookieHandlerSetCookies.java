package com.atexpose.dispatcher.channels.web_channel;

import com.atexpose.util.web_cookie.WebCookieHandler;

import java.util.Map;

/**
 * The purpose of this class is to be able to access the method to set cookies to send
 * to client and close the request-response without making these methods available
 * outside of @expose
 */
class WebCookieHandlerSetCookies extends WebCookieHandler {
    static void setCookiesFromClient(Map<String, String> cookies) {
        setCookiesFromClientInternal(cookies);
    }

    static void closeRequestResponse() {
        closeRequestResponseInternal();
    }
}