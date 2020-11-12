package com.atexpose.dispatcher.channels.webchannel;

import java.util.List;
import java.util.Map;

/**
 * This is used to read and write cookies in Java or Kotlin code, i.e.
 * outside @expose code. It only works in conjunction with the web server,
 * i.e. a WebChannel as channel and WebWrapper as wrapper
 *
 * <p>
 * Usage:
 * <p>
 * To read a cookie:
 * String cookieValue = WebSession.getIncomingCookie("my_cookie_name");
 * To write a cookie:
 * WebSessionCookie cookie = WebSessionCookie.builder()
 * .name("my_cookie_name")
 * .value("1234")
 * .expires(Instant.now().plusSeconds(60 * 20))
 * .build();
 * WebSession.addCookieToSet(cookie);
 * <p>
 * <p>
 * Life cycle of a request response
 * In WebChannel:
 * WebSession.setCookiesFromClient(cookies)
 * <p>
 * In code outside of @expose
 * WebSession.getIncomingCookie("name_of_cookie")
 * WebSession.getIncomingCookie("name_of_second_cookie")
 * WebSession.addCookieToSendToClient(cookieOne)
 * <p>
 * In WebWrapper:
 * WebSession.getCookiesToSendToClient()
 * In WebChannel:
 * WebSession.closeSession()
 */
public class WebCookieStorage {
    static final WebCookieStorageInternal WEB_COOKIE_STORAGE_INTERNAL = new WebCookieStorageInternal();

    // Private constructor to prevent incorrect usage
    private WebCookieStorage() {
    }


    //------------------------------------------------------------------------
    // Methods used outside of @expose
    //------------------------------------------------------------------------

    /**
     * @param cookieName The name of the cookie
     * @return The value of the cookie with the argument name
     */
    public static String getIncomingCookie(String cookieName) {
        return WEB_COOKIE_STORAGE_INTERNAL.getIncomingCookie(cookieName, threadName());
    }

    /**
     * Argument cookie will be sent to client as a part of the response of the
     * current thread
     *
     * @param cookie A cookie to send to client
     */
    public static void addCookieToSendToClient(WebSessionCookie cookie) {
        WEB_COOKIE_STORAGE_INTERNAL.addCookieToSendToClient(cookie, threadName());
    }


    //------------------------------------------------------------------------
    // Methods used by @expose
    //------------------------------------------------------------------------


    /**
     * @param cookies Cookies that came from the client
     */
    static void setCookiesFromClient(Map<String, String> cookies) {
        WEB_COOKIE_STORAGE_INTERNAL.setCookiesFromClient(cookies, threadName());
    }


    /**
     * @return The cookies to send to the client
     */
    public static List<WebSessionCookie> getCookiesToSendToClient() {
        return WEB_COOKIE_STORAGE_INTERNAL.getCookiesToSendToClient(threadName());
    }


    /**
     * Clears the data for this thread.
     */
    static void closeSession() {
        WEB_COOKIE_STORAGE_INTERNAL.closeSession(threadName());
    }


    private static String threadName() {
        return Thread.currentThread().getName();
    }
}