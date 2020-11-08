package com.atexpose.dispatcher.channels.webchannel;

import java.util.HashMap;
import java.util.Map;

public class WebSession {
    private static final Map<String, Map<String, String>> INCOMING_COOKIES = new HashMap<>();
    private static final Map<String, Map<String, String>> COOKIES_TO_SET = new HashMap<>();

    static void setIncomingCookies(Map<String, String> cookies) {
        final HashMap<String, String> map = new HashMap<>(cookies);
        WebSession.INCOMING_COOKIES
                .put(threadName(), map);
    }


    public static String getIncomingCookie(String cookieName) {
        return WebSession.INCOMING_COOKIES
                .get(threadName())
                .get(cookieName);
    }


    public static void addCookieToSet(String cookieName, String cookieValue) {
        if (!WebSession.COOKIES_TO_SET.containsKey(threadName())) {
            WebSession.COOKIES_TO_SET.put(threadName(), new HashMap<>());
        }
        WebSession.COOKIES_TO_SET
                .get(threadName())
                .put(cookieName, cookieValue);
    }


    static Map<String, String> getCookiesToSet() {
        return WebSession.COOKIES_TO_SET.get(threadName());
    }


    static void closeSession() {
        Map<String, String> incomingCookiesForCurrentThread = INCOMING_COOKIES.get(threadName());
        if (incomingCookiesForCurrentThread != null) {
            incomingCookiesForCurrentThread.clear();
        }
        Map<String, String> cookiesToWriteForCurrentThread = COOKIES_TO_SET.get(threadName());
        if (cookiesToWriteForCurrentThread != null) {
            cookiesToWriteForCurrentThread.clear();
        }
    }


    private static String threadName() {
        return Thread.currentThread().getName();
    }
}
