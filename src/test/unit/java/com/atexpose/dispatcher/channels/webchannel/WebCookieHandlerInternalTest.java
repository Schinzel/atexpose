package com.atexpose.dispatcher.channels.webchannel;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.RandomUtil;
import lombok.val;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class WebCookieHandlerInternalTest {


    //------------------------------------------------------------------------
    // getIncomingCookie
    //------------------------------------------------------------------------

    @Test
    public void getIncomingCookie_cookieNameNull_Exception() {
        val webCookie = new WebCookieHandlerInternal();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookie.getIncomingCookie(null, "any_thread_name")
        );
    }

    @Test
    public void getIncomingCookie_cookieNameEmptyString_Exception() {
        val webCookie = new WebCookieHandlerInternal();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookie.getIncomingCookie("", "any_thread_name")
        );
    }

    @Test
    public void getIncomingCookie_cookieNameDoesNotExist_Null() {
        val webCookie = new WebCookieHandlerInternal();
        val threadName = getRandomString();
        webCookie.setCookiesFromClient(Collections.emptyMap(), threadName);
        val actual = webCookie.getIncomingCookie("no_such_cookie", threadName);
        assertThat(actual).isNull();
    }


    @Test
    public void getIncomingCookie_threadDoesNotExist_Exception() {
        val webCookie = new WebCookieHandlerInternal();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookie.getIncomingCookie("any_cookie_name", "no_such_thread")
        );
    }

    @Test
    public void getIncomingCookie_cookieNameDoesExist_SetCookieFound() {
        val webCookie = new WebCookieHandlerInternal();
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
        val webCookieStorage = new WebCookieHandlerInternal();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookieStorage.addCookieToSendToClient(null, "any_thread_name")
        );
    }

    @Test
    public void addCookieToSendToClient_threadNameNull_Exception() {
        val webCookieStorage = new WebCookieHandlerInternal();
        val cookie = getCookie();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webCookieStorage.addCookieToSendToClient(cookie, null)
        );
    }


    @Test
    public void addCookieToSendToClient_addOneCookieInOneThread_CookieExitsInCollection() {
        val webCookieStorage = new WebCookieHandlerInternal();
        val cookie = getCookie();
        webCookieStorage.addCookieToSendToClient(cookie, "my_thread_name");
        final boolean cookieExists = webCookieStorage.mCookiesToSendToClient
                .get("my_thread_name")
                .contains(cookie);
        assertThat(cookieExists).isTrue();

    }

    @Test
    public void addCookieToSendToClient_add1000Cookies_collectionsHas1000Cookies() {
        val webCookieStorage = new WebCookieHandlerInternal();
        for (int i = 0; i < 1000; i++) {
            webCookieStorage.addCookieToSendToClient(getCookie(), "thread_i" + i);
        }
        assertThat(webCookieStorage.mCookiesToSendToClient.size()).isEqualTo(1000);
    }

    @Test
    public void addCookieToSendToClient_add1CookieIn1000Threads_collectionsHas1000ThreadsWithOneCookieEach() {
        val webCookieStorage = new WebCookieHandlerInternal();
        for (int i = 0; i < 1000; i++) {
            val cookie = WebCookie.builder()
                    .name("cookie_" + i)
                    .value(RandomUtil.getRandomString(10))
                    .expires(Instant.now())
                    .build();
            webCookieStorage.addCookieToSendToClient(cookie, "thread_" + i);
        }
        for (int i = 0; i < 1000; i++) {
            val headerString = webCookieStorage.mCookiesToSendToClient
                    .get("thread_" + i)
                    .get(0)
                    .getHttpHeaderSetCookieString();
            assertThat(headerString).contains("cookie_" + i);
        }
    }


    //------------------------------------------------------------------------
    // util
    //------------------------------------------------------------------------
    private static String getRandomString() {
        val stringLength = RandomUtil.getRandomNumber(1, 100);
        return RandomUtil.getRandomString(stringLength);
    }

    private static WebCookie getCookie() {
        return WebCookie.builder()
                .name(RandomUtil.getRandomString(10))
                .value(RandomUtil.getRandomString(10))
                .expires(Instant.now())
                .build();
    }
}