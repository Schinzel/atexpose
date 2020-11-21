package com.atexpose.util.web_cookie;

import io.schinzel.basicutils.thrower.Thrower;
import lombok.val;

import java.util.*;

/**
 * The purpose of this class is to handle cookies per thread
 */
class WebCookieHandlerInternal {
    /**
     * Holds the incoming cookies that where a part of the request
     * Key - the name of a thread
     * Value - A map with cookie keys and values
     */
    final Map<String, Map<String, String>> mCookiesFromClient = new HashMap<>();
    /**
     * Holds cookies to send to the browser
     * Key - the name of the thread
     * Value - A list of cookies to send to the client
     */
    final Map<String, List<WebCookie>> mCookiesToSendToClient = new HashMap<>();

    //------------------------------------------------------------------------
    // Methods used outside of @expose (via WebCookieHandler)
    //------------------------------------------------------------------------


    String getIncomingCookie(String cookieName, String threadName) {
        try {
            Thrower.createInstance()
                    .throwIfVarEmpty(cookieName, "cookieName")
                    .throwIfVarEmpty(threadName, "threadName");
            val currentThreadsCookies = mCookiesFromClient.get(threadName);
            if (currentThreadsCookies == null) {
                throw new RuntimeException("No cookies for thread");
            }
            return currentThreadsCookies.get(cookieName);
        } catch (Exception e) {
            val errorMessage = "Error when requesting incoming cookie named '"
                    + cookieName + "' in thread '" + threadName + "'. ";
            throw new RuntimeException(errorMessage + e.getMessage());
        }
    }


    void addCookieToSendToClient(WebCookie cookie, String threadName) {
        try {
            Thrower.createInstance()
                    .throwIfVarNull(cookie, "cookie")
                    .throwIfVarEmpty(threadName, "threadName");
            // If the current thread is not in the map
            if (!mCookiesToSendToClient.containsKey(threadName)) {
                // Add an entry for current thread
                mCookiesToSendToClient.put(threadName, new ArrayList<>());
            }
            // Add the argument cookie to the current thread list of cookie to send to client
            mCookiesToSendToClient
                    .get(threadName)
                    .add(cookie);
        } catch (Exception e) {
            val errorMessage = "Error when adding a cookie to send to client. '"
                    + "' in thread '" + threadName + "'. ";
            throw new RuntimeException(errorMessage + e.getMessage());
        }
    }


    //------------------------------------------------------------------------
    // Methods used by @expose (via WebCookieHandler)
    //------------------------------------------------------------------------

    void setCookiesFromClient(Map<String, String> cookies, String threadName) {
        Thrower.throwIfVarEmpty(threadName, "threadName");
        if (cookies == null) {
            cookies = Collections.emptyMap();
        }
        final HashMap<String, String> map = new HashMap<>(cookies);
        mCookiesFromClient.put(threadName, map);
    }


    List<WebCookie> getCookiesToSendToClient(String threadName) {
        Thrower.throwIfVarEmpty(threadName, "threadName");
        return mCookiesToSendToClient.get(threadName);
    }


    void closeSession(String threadName) {
        Thrower.throwIfVarEmpty(threadName, "threadName");
        // Get map for cookies-from-client
        Map<String, String> currentThreadsCookiesFromClient = mCookiesFromClient.get(threadName);
        // If there was a map
        if (currentThreadsCookiesFromClient != null) {
            // Clear the map
            currentThreadsCookiesFromClient.clear();
        }
        // Get map for cookies-to-send-to-client
        List<WebCookie> currentThreadsCookiesToSendToClient = mCookiesToSendToClient.get(threadName);
        // If there was such a map
        if (currentThreadsCookiesToSendToClient != null) {
            // Clear the map
            currentThreadsCookiesToSendToClient.clear();
        }
    }
}
