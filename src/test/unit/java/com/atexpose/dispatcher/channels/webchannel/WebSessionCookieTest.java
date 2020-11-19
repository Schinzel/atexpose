package com.atexpose.dispatcher.channels.webchannel;

import io.schinzel.basicutils.RandomUtil;
import lombok.val;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.*;


public class WebSessionCookieTest {

    @Test
    public void getExpiresDate() {
        val instant = Instant.parse("2020-11-12T21:59:59.50Z");
        val dateTimeString = WebSessionCookie.getExpiresAsString(instant);
        val expected = "Thu, 12 Nov 2020 21:59:59 GMT";
        assertThat(dateTimeString).isEqualTo(expected);
    }


    @Test
    public void getHttpHeaderSetCookieString() {
        val cookieValue = RandomUtil.getRandomString(5);
        val actual = WebSessionCookie.builder()
                .name("cookie_name")
                .value(cookieValue)
                .expires(Instant.parse("2120-11-12T21:59:59.50Z"))
                .build()
                .getHttpHeaderSetCookieString();
        val expected = "Set-Cookie: cookie_name=" + cookieValue + "; Path=/; Expires=Tue, 12 Nov 2120 21:59:59 GMT\r\n";
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void builder_nameHasAllowedChars_NoException() {
        assertThatCode(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(Instant.now())
                        .build()
        ).doesNotThrowAnyException();
    }

    @Test
    public void builder_nameHasForbiddenChars_NoException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("åäö")
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }


    @Test
    public void builder_expiresIsBeforeNow_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }

    @Test
    public void builder_instantIsNull_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(null)
                        .build()
        );
    }


    @Test
    public void builder_nameIsEmptyString_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("")
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }

    @Test
    public void builder_nameIsTooLong_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name(RandomUtil.getRandomString(101))
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }

    @Test
    public void builder_nameIsNull_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name(null)
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }


    @Test
    public void builder_valueHasAllowedChars_NoException() {
        assertThatCode(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(Instant.now())
                        .build()
        ).doesNotThrowAnyException();
    }

    @Test
    public void builder_valueHasForbiddenChars_NoException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value("åäö")
                        .expires(Instant.now())
                        .build()
        );
    }


    @Test
    public void builder_valueIsEmptyString_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value("")
                        .expires(Instant.now())
                        .build()
        );
    }

    @Test
    public void builder_valueIsTooLong_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value(RandomUtil.getRandomString(105))
                        .expires(Instant.now())
                        .build()
        );
    }

    @Test
    public void builder_valueIsNull_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebSessionCookie.builder()
                        .name("my_name")
                        .value(null)
                        .expires(Instant.now())
                        .build()
        );
    }
}