package io.schinzel.samples.auxiliary;

import com.atexpose.Expose;
import com.atexpose.util.web_cookie.ResponseCookie;
import com.atexpose.util.web_cookie.WebCookieHandler;
import io.schinzel.basicutils.RandomUtil;

import java.time.Instant;

/**
 * The purpose of this class it to hold sample static methods that will be exposed.
 */
public class MyClass {

    @Expose
    public static String sayIt() {
        return "Helloooo world!";
    }


    @Expose(
            arguments = {"Int"},
            requiredArgumentCount = 1
    )
    public static int doubleIt(int i) {
        return i * 2;
    }


    @Expose(
            arguments = {"Boolean"},
            requiredArgumentCount = 1
    )
    public static String isTrue(boolean i) {
        return "It was "  + i;
    }


    @Expose(
            arguments = {"String"}
    )
    public static String doEcho(String str) {
        return "Echo: " + str;
    }


    //------------------------------------------------------------------------
    // Cookie demo
    //------------------------------------------------------------------------

    @Expose
    public static void setCookie() {
        final ResponseCookie responseCookie = ResponseCookie.builder()
                .name("my_cookie")
                .value(RandomUtil.getRandomString(5))
                .expires(Instant.now().plusSeconds(20 * 60))
                .build();
        WebCookieHandler.addResponseCookie(responseCookie);
    }

    @Expose
    public static void readCookie() {
        final String cookieValue = WebCookieHandler
                .getRequestCookie("my_cookie")
                .getValue();
        System.out.println("Value of cookie part of the request '" + cookieValue + "'");
    }
}
