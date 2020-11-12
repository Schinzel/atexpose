package com.atexpose.dispatcher.channels.webchannel;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.RandomUtil;
import lombok.val;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class WebCookieStorageInternalTest {


    //------------------------------------------------------------------------
    // getIncomingCookie
    //------------------------------------------------------------------------

    @Test
    public void getIncomingCookie_cookieNameNull_Exception() {
        val webCookie = new WebCookieStorageInternal();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookie.getIncomingCookie(null, "any_thread_name")
        );
    }

    @Test
    public void getIncomingCookie_cookieNameEmptyString_Exception() {
        val webCookie = new WebCookieStorageInternal();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookie.getIncomingCookie("", "any_thread_name")
        );
    }

    @Test
    public void getIncomingCookie_cookieNameDoesNotExist_Null() {
        val webCookie = new WebCookieStorageInternal();
        val threadName = getRandomString();
        webCookie.setCookiesFromClient(Collections.emptyMap(), threadName);
        val actual = webCookie.getIncomingCookie("no_such_cookie", threadName);
        assertThat(actual).isNull();
    }


    @Test
    public void getIncomingCookie_threadDoesNotExist_Exception() {
        val webCookie = new WebCookieStorageInternal();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookie.getIncomingCookie("any_cookie_name", "no_such_thread")
        );
    }

    @Test
    public void getIncomingCookie_cookieNameDoesExist_SetCookieFound() {
        val webCookie = new WebCookieStorageInternal();
        val cookieName = getRandomString();
        val cookieValue = getRandomString();
        val threadName = getRandomString();
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put(cookieName, cookieValue)
                .build();
        webCookie.setCookiesFromClient(map, threadName);
        val actual = webCookie.getIncomingCookie(cookieName, threadName);
        assertThat(actual).isEqualTo(cookieValue);
    }


    //------------------------------------------------------------------------
    // addCookieToSendToClient
    //------------------------------------------------------------------------

    @Test
    public void addCookieToSendToClient_cookieNull_Exception() {

    }

    @Test
    public void addCookieToSendToClient_threadNameNull_Exception() {

    }


    @Test
    public void addCookieToSendToClient_threadDoesNotExist_Exception() {

    }


    @Test
    public void addCookieToSendToClient_addOneCookieInOneThread_CookieExitsInCollection() {

    }

    @Test
    public void addCookieToSendToClient_add1000Cookies_collectionsHas1000Cookies() {

    }


    @Test
    public void addCookieToSendToClient_add1CookieIn1000Threads_collectionsHas1000ThreadsWithOneCookieEach() {

    }


    //------------------------------------------------------------------------
    // util
    //------------------------------------------------------------------------
    private static String getRandomString() {
        val stringLength = RandomUtil.getRandomNumber(1, 100);
        return RandomUtil.getRandomString(stringLength);
    }
}