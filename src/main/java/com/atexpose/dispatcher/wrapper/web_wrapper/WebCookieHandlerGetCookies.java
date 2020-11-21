package com.atexpose.dispatcher.wrapper.web_wrapper;

import com.atexpose.util.web_cookie.WebCookie;
import com.atexpose.util.web_cookie.WebCookieHandler;

import java.util.List;

/**
 * The purpose of this class is to be able to access the method to get cookies to send
 * to client without making this method available outside of @expose
 */
class WebCookieHandlerGetCookies extends WebCookieHandler {

    static List<WebCookie> getCookiesToSendToClient() {
        return getCookiesToSendToClientInternal();
    }
}