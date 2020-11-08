package com.atexpose.dispatcher.channels.webchannel;

import java.util.HashMap;
import java.util.Map;

public class WebSession {
    private static final Map<String, Map<String, String>> INCOMING_COOKIES = new HashMap<>();
    private static final Map<String, Map<String, String>> COOKIES_TO_WRITE = new HashMap<>();

    static String getIncomingCookie(String cookieName) {
        return WebSession.INCOMING_COOKIES
                .get(threadName())
                .get(cookieName);
    }


    static void setIncomingCookies(Map<String, String> cookies) {
//        System.out.println(threadName() + " setting cookies");
//        WebSession.INCOMING_COOKIES
//                .put(threadName(), cookies);
    }


    static void addCookieToWrite(String cookieName, String cookieValue) {
        if (!WebSession.COOKIES_TO_WRITE.containsKey(threadName())) {
            WebSession.COOKIES_TO_WRITE.put(threadName(), new HashMap<>());
        }
        WebSession.COOKIES_TO_WRITE
                .get(threadName())
                .put(cookieName, cookieValue);
    }


    static Map<String, String> getCookiesToWrite() {
        return WebSession.COOKIES_TO_WRITE.get(threadName());
    }


    static void closeSession() {
//        System.out.println(threadName() + " clearing cookies");
//        Map<String, String> incomingCookiesForCurrentThread = INCOMING_COOKIES.get(threadName());
//        if (incomingCookiesForCurrentThread != null) {
//            incomingCookiesForCurrentThread.clear();
//        }
//        Map<String, String> cookiesToWriteForCurrentThread = COOKIES_TO_WRITE.get(threadName());
//        if (cookiesToWriteForCurrentThread != null) {
//            cookiesToWriteForCurrentThread.clear();
//        }
    }


    private static String threadName() {
        return Thread.currentThread().getName();
    }
}