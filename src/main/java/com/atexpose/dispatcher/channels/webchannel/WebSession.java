package com.atexpose.dispatcher.channels.webchannel;

import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
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
public class WebSession {
    /**
     * Holds the incoming cookies that where a part of the request
     * Key - the name of a thread
     * Value - A map with cookie keys and values
     */
    private static final Map<String, Map<String, String>> COOKIES_FROM_CLIENT
            = new HashMap<>();
    /**
     * Holds cookies to send to the browser
     * Key - the name of the thread
     * Value - A list of cookies to send to the client
     */
    private static final Map<String, List<WebSessionCookie>> COOKIES_TO_SEND_TO_CLIENT
            = new HashMap<>();

    // Private constructor to prevent incorrect usage
    private WebSession() {
    }

    //------------------------------------------------------------------------
    // Methods used outside of @expose
    //------------------------------------------------------------------------

    /**
     * @param cookieName The name of the cookie
     * @return The value of the cookie with the argument name
     */
    public static String getIncomingCookie(String cookieName) {
        return WebSession.COOKIES_FROM_CLIENT
                .get(threadName())
                .get(cookieName);
    }

    /**
     * Argument cookie will be sent to client as a part of the response of the
     * current thread
     *
     * @param cookie A cookie to send to client
     */
    public static void addCookieToSendToClient(WebSessionCookie cookie) {
        // Get name of current thread
        val threadName = threadName();
        // If the current thread is not in the map
        if (!WebSession.COOKIES_TO_SEND_TO_CLIENT.containsKey(threadName)) {
            // Add an entry for current thread
            WebSession.COOKIES_TO_SEND_TO_CLIENT.put(threadName, new ArrayList<>());
        }
        // Add the argument cookie to the current thread list of cookie to send to client
        WebSession.COOKIES_TO_SEND_TO_CLIENT
                .get(threadName)
                .add(cookie);
    }


    //------------------------------------------------------------------------
    // Methods used by @expose
    //------------------------------------------------------------------------


    /**
     * @param cookies Cookies that came from the client
     */
    static void setCookiesFromClient(Map<String, String> cookies) {
        final HashMap<String, String> map = new HashMap<>(cookies);
        WebSession.COOKIES_FROM_CLIENT
                .put(threadName(), map);
    }


    /**
     * @return The cookies to send to the client
     */
    public static List<WebSessionCookie> getCookiesToSendToClient() {
        val threadName = threadName();
        return WebSession
                .COOKIES_TO_SEND_TO_CLIENT.get(threadName);
    }


    /**
     * Clears the data for this thread.
     */
    static void closeSession() {
        // Get map for cookies-from-client
        Map<String, String> currentThreadsCookiesFromClient = COOKIES_FROM_CLIENT.get(threadName());
        // If there was a map
        if (currentThreadsCookiesFromClient != null) {
            // Clear the map
            currentThreadsCookiesFromClient.clear();
        }
        // Get map for cookies-to-send-to-client
        List<WebSessionCookie> currentThreadsCookiesToSendToClient = COOKIES_TO_SEND_TO_CLIENT.get(threadName());
        // If there was such a map
        if (currentThreadsCookiesToSendToClient != null) {
            // Clear the map
            currentThreadsCookiesToSendToClient.clear();
        }
    }


    private static String threadName() {
        return Thread.currentThread().getName();
    }
}