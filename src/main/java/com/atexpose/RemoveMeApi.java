package com.atexpose;

import com.atexpose.util.web_cookie.WebCookieHandler;
import com.atexpose.util.web_cookie.ResponseCookie;
import io.schinzel.basicutils.RandomUtil;
import lombok.val;

import java.time.Instant;

@SuppressWarnings("unused")
public class RemoveMeApi {

    @Expose(
            arguments = {"String", "Int"},
            requiredArgumentCount = 2
    )
    public static String concat(String str1, Integer integer) {
        return str1 + integer.toString();
    }

    @Expose(
            arguments = {"test_var"},
            requiredArgumentCount = 1
    )
    public static RemoveMeVar test_it(RemoveMeVar test_var) {
        String cookieValue = WebCookieHandler
                .getRequestCookie("my_cookie")
                .getValue();
        val cookie = ResponseCookie.builder()
                .name("my_funky_cookie_" + RandomUtil.getRandomString(2))
                .value("kalle kutta")
                .expires(Instant.now().plusSeconds(60 * 20))
                .build();
        WebCookieHandler.addResponseCookie(cookie);
        return new RemoveMeVar(test_var.s, test_var.i + 10);
    }


    @Expose(
            arguments = {"test_enum"}
    )
    public static RemoveMeEnum test_it_2(RemoveMeEnum test_enum) {
        val cookie = ResponseCookie.builder()
                .name("my_second_funky_cookie")
                .value(RandomUtil.getRandomString(5))
                .expires(Instant.now().plusSeconds(60 * 10))
                .build();
        WebCookieHandler.addResponseCookie(cookie);
        return RemoveMeEnum.SECOND;
    }


    @Expose(
            arguments = {"test_arg"}
    )
    public static String bapp(String str) {
        return str + " bapp!";
    }


    @Expose(
            arguments = {"test_arg"}
    )
    public static void bapp2(String str) {
    }

}
