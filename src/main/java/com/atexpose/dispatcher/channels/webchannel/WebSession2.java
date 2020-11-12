package com.atexpose.dispatcher.channels.webchannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class WebSession2 {
    /**
     * Holds the incoming cookies that where a part of the request
     * Key - the name of a thread
     * Value - A map with cookie keys and values
     */
    final Map<String, Map<String, String>> cookiesFromClient = new HashMap<>();
    /**
     * Holds cookies to send to the browser
     * Key - the name of the thread
     * Value - A list of cookies to send to the client
     */
    final Map<String, List<WebSessionCookie>> cookiesToSendToClient = new HashMap<>();


    String getIncomingCookie(String cookieName, String threadName) {
        return WebSession.COOKIES_FROM_CLIENT
                .get(threadName)
                .get(cookieName);
    }


    void addCookieToSendToClient(WebSessionCookie cookie, String threadName) {
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


    void setCookiesFromClient(Map<String, String> cookies, String threadName) {
        final HashMap<String, String> map = new HashMap<>(cookies);
        WebSession.COOKIES_FROM_CLIENT.put(threadName, map);
    }


    List<WebSessionCookie> getCookiesToSendToClient(String threadName) {
        return WebSession.COOKIES_TO_SEND_TO_CLIENT.get(threadName);
    }


    void closeSession(String threadName) {
        // Get map for cookies-from-client
        Map<String, String> currentThreadsCookiesFromClient = cookiesFromClient.get(threadName);
        // If there was a map
        if (currentThreadsCookiesFromClient != null) {
            // Clear the map
            currentThreadsCookiesFromClient.clear();
        }
        // Get map for cookies-to-send-to-client
        List<WebSessionCookie> currentThreadsCookiesToSendToClient = cookiesToSendToClient.get(threadName);
        // If there was such a map
        if (currentThreadsCookiesToSendToClient != null) {
            // Clear the map
            currentThreadsCookiesToSendToClient.clear();
        }
    }
}
