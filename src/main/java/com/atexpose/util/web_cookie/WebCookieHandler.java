package com.atexpose.util.web_cookie;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * The purpose of this class is read and write cookies in Java or Kotlin code outside @expose code.
 * </p>
 * <p>
 * It only works in conjunction with the web server, i.e. a WebChannel as channel and WebWrapper as wrapper
 *</p>
 * <p>
 * <b>Usage</b>
 * <br>
 * To read a cookie:
 * <pre>
 *     String cookieValue = WebCookieHandler.getRequestCookieValue("my_cookie_name");
 * </pre>
 * To write a cookie:
 * <pre>
 *     WebCookie cookie = WebCookie.builder()
 *      .name("my_cookie_name")
 *      .value("1234")
 *      .expires(Instant.now().plusSeconds(60 * 20))
 *      .build();
 *     WebCookieHandler.addResponseCookie(cookie);
 * </pre>
 *
 * </p>
 *
 * <b>Internal documentation</b>
 * <p>
 * The somewhat contrived naming is due to that there already is a CookieHandler and
 * AtExposeCookieHandler is kind of long. It is used in conjunction with the web server which
 * uses a WebChannel and a WebWrapper.
 * </p>
 * <p>
 * Life cycle of a request response
 * </p>
 * <p>
 * In WebChannel:
 * <pre>
 *     WebCookieHandlerSetCookies.setRequestCookies(cookies)
 * </pre>
 * In code outside of @expose
 * <pre>
 *     WebCookieHandler.getRequestCookie("name_of_cookie")
 *     WebCookieHandler.getRequestCookie("name_of_second_cookie")
 *     WebCookieHandler.addResponseCookie(cookieOne)
 * </pre>
 * In WebWrapper:
 * <pre>
 *     WebCookieHandlerGetCookies.getResponseCookies()
 * </pre>
 * In WebChannel:
 * <pre>
 *     WebCookieHandlerSetCookies.closeRequestResponse()
 * </pre>
 * </p>
 */
public class WebCookieHandler {
    private static final WebCookieHandlerInternal WEB_COOKIE_HANDLER_INTERNAL = new WebCookieHandlerInternal();

    // Protected constructor to prevent incorrect usage
    protected WebCookieHandler() {
    }


    //------------------------------------------------------------------------
    // Methods used outside of @expose
    //------------------------------------------------------------------------

    /**
     * @param cookieName The name of the cookie
     * @return The cookie with the argument name that was part of the request
     */
    public static RequestCookie getRequestCookie(String cookieName) {
        return WEB_COOKIE_HANDLER_INTERNAL.getRequestCookie(cookieName, threadName());
    }


    /**
     * Argument cookie will be sent to client as a part of the response of the
     * current thread
     *
     * @param cookie A cookie to send to client
     */
    public static void addResponseCookie(ResponseCookie cookie) {
        WEB_COOKIE_HANDLER_INTERNAL.addResponseCookie(cookie, threadName());
    }


    //------------------------------------------------------------------------
    // Methods used by @expose
    // These are protected so these methods will not be accessible outside of @expose.
    // But this class is extended inside of @expose as to make these methods accessible
    // inside of @expose.
    //------------------------------------------------------------------------

    /**
     * @param cookies Cookies that came from the client
     */
    protected static void setRequestCookiesProtected(Map<String, String> cookies) {
        WEB_COOKIE_HANDLER_INTERNAL.setRequestCookies(cookies, threadName());
    }


    /**
     * @return The cookies to send to the client
     */
    protected static List<ResponseCookie> getResponseCookiesProtected() {
        return WEB_COOKIE_HANDLER_INTERNAL.getResponseCookies(threadName());
    }


    /**
     * Clears the data for this thread.
     */
    protected static void closeRequestResponseInternal() {
        WEB_COOKIE_HANDLER_INTERNAL.closeRequestResponse(threadName());
    }


    //------------------------------------------------------------------------
    // Private method
    //------------------------------------------------------------------------

    private static String threadName() {
        return Thread.currentThread().getName();
    }
}