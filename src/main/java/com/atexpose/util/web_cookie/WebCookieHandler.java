package com.atexpose.util.web_cookie;

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
 * String cookieValue = WebCookieHandler.getIncomingCookie("my_cookie_name");
 * To write a cookie:
 * WebSessionCookie cookie = WebCookie.builder()
 * .name("my_cookie_name")
 * .value("1234")
 * .expires(Instant.now().plusSeconds(60 * 20))
 * .build();
 * WebCookieHandler.addCookieToSet(cookie);
 * <p>
 * <p>
 * Life cycle of a request response
 * In WebChannel:
 * WebCookieHandler.setCookiesFromClient(cookies)
 * <p>
 * In code outside of @expose
 * WebCookieHandler.getIncomingCookie("name_of_cookie")
 * WebCookieHandler.getIncomingCookie("name_of_second_cookie")
 * WebCookieHandler.addCookieToSendToClient(cookieOne)
 * <p>
 * In WebWrapper:
 * WebCookieHandler.getCookiesToSendToClient()
 * In WebChannel:
 * WebCookieHandler.closeSession()
 */
public class WebCookieHandler {
    static final WebCookieHandlerInternal WEB_COOKIE_HANDLER_INTERNAL = new WebCookieHandlerInternal();

    // Private constructor to prevent incorrect usage
    private WebCookieHandler() {
    }


    //------------------------------------------------------------------------
    // Methods used outside of @expose
    //------------------------------------------------------------------------

    /**
     * @param cookieName The name of the cookie
     * @return The value of the cookie with the argument name
     */
    public static String getIncomingCookie(String cookieName) {
        return WEB_COOKIE_HANDLER_INTERNAL.getIncomingCookie(cookieName, threadName());
    }


    /**
     * Argument cookie will be sent to client as a part of the response of the
     * current thread
     *
     * @param cookie A cookie to send to client
     */
    public static void addCookieToSendToClient(WebCookie cookie) {
        WEB_COOKIE_HANDLER_INTERNAL.addCookieToSendToClient(cookie, threadName());
    }



    //------------------------------------------------------------------------
    // Methods used by @expose
    //------------------------------------------------------------------------

    /**
     * @param cookies Cookies that came from the client
     */
    public static void setCookiesFromClient(Map<String, String> cookies) {
        WEB_COOKIE_HANDLER_INTERNAL.setCookiesFromClient(cookies, threadName());
    }


    /**
     * @return The cookies to send to the client
     */
    public static List<WebCookie> getCookiesToSendToClient() {
        return WEB_COOKIE_HANDLER_INTERNAL.getCookiesToSendToClient(threadName());
    }


    /**
     * Clears the data for this thread.
     */
    public static void closeSession() {
        WEB_COOKIE_HANDLER_INTERNAL.closeSession(threadName());
    }


    private static String threadName() {
        return Thread.currentThread().getName();
    }
}