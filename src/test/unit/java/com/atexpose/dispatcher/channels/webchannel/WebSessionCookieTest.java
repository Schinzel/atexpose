package com.atexpose.dispatcher.channels.webchannel;

import io.schinzel.basicutils.RandomUtil;
import lombok.val;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class WebSessionCookieTest {

    @Test
    public void getExpiresDate() {
        val instant = Instant.parse("2020-11-12T21:59:59.50Z");
        val dateTimeString = WebSessionCookie.getExpiresDate(instant);
        val expected = "Thu, 12 Nov 2020 21:59:59 GMT";
        assertThat(dateTimeString).isEqualTo(expected);
    }


    @Test
    public void getHttpHeaderSetCookieString() {
        val cookieValue = RandomUtil.getRandomString(5);
        val actual = WebSessionCookie.builder()
                .name("cookie_name")
                .value(cookieValue)
                .expires(Instant.parse("2020-11-12T21:59:59.50Z"))
                .build()
                .getHttpHeaderSetCookieString();
        val expected = "Set-Cookie: cookie_name=" + cookieValue + "; Path=/; Expires=Thu, 12 Nov 2020 21:59:59 GMT\r\n";
        assertThat(actual).isEqualTo(expected);
    }
}