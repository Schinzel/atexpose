package com.atexpose.dispatcher.channels.webchannel;

import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSession {
    private static final Map<String, Map<String, String>> INCOMING_COOKIES = new HashMap<>();
    private static final Map<String, List<WebSessionCookie>> COOKIES_TO_SET = new HashMap<>();

    private WebSession(){}

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


    public static void addCookieToSet(WebSessionCookie cookie) {
        val threadName = threadName();
        if (!WebSession.COOKIES_TO_SET.containsKey(threadName)) {
            WebSession.COOKIES_TO_SET.put(threadName, new ArrayList<>());
        }
        WebSession.COOKIES_TO_SET
                .get(threadName)
                .add(cookie);
    }


    public static List<WebSessionCookie> getCookiesToSet() {
        val threadName = threadName();
        val cookie = WebSession.COOKIES_TO_SET.get(threadName);
        return cookie;
    }


    static void closeSession() {
        Map<String, String> incomingCookiesForCurrentThread = INCOMING_COOKIES.get(threadName());
        if (incomingCookiesForCurrentThread != null) {
            incomingCookiesForCurrentThread.clear();
        }
        List<WebSessionCookie> cookiesToSetForCurrentThread = COOKIES_TO_SET.get(threadName());
        if (cookiesToSetForCurrentThread != null) {
            cookiesToSetForCurrentThread.clear();
        }
    }


    private static String threadName() {
        return Thread.currentThread().getName();
    }


}


