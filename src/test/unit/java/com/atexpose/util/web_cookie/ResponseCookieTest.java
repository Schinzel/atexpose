package com.atexpose.util.web_cookie;

import io.schinzel.basicutils.RandomUtil;
import lombok.val;
import org.junit.Test;
import java.time.Instant;
import static org.assertj.core.api.AssertionsForClassTypes.*;


public class ResponseCookieTest {

    @Test
    public void getExpiresDate() {
        val instant = Instant.parse("2020-11-12T21:59:59.50Z");
        val dateTimeString = ResponseCookie.getExpiresAsString(instant);
        val expected = "Thu, 12 Nov 2020 21:59:59 GMT";
        assertThat(dateTimeString).isEqualTo(expected);
    }


    @Test
    public void getHttpHeaderSetCookieString() {
        val cookieValue = RandomUtil.getRandomString(5);
        val actual = ResponseCookie.builder()
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
                ResponseCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(Instant.now())
                        .build()
        ).doesNotThrowAnyException();
    }

    @Test
    public void builder_nameHasForbiddenChars_NoException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("åäö")
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }


    @Test
    public void builder_expiresIsBeforeNow_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }

    @Test
    public void builder_instantIsNull_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(null)
                        .build()
        );
    }


    @Test
    public void builder_nameIsEmptyString_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("")
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }

    @Test
    public void builder_nameIsTooLong_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name(RandomUtil.getRandomString(101))
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }

    @Test
    public void builder_nameIsNull_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name(null)
                        .value("my_value")
                        .expires(Instant.now().minusSeconds(100))
                        .build()
        );
    }


    @Test
    public void builder_valueHasAllowedChars_NoException() {
        assertThatCode(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value("my_value")
                        .expires(Instant.now())
                        .build()
        ).doesNotThrowAnyException();
    }


    @Test
    public void builder_allAllowedSpecialCharsInValue_NoException() {
        assertThatCode(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value("#:\"-_{}[],")
                        .expires(Instant.now())
                        .sameSite(SameSite.STRICT)
                        .build()
        ).doesNotThrowAnyException();
    }

    @Test
    public void builder_valueHasForbiddenChars_NoException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value("åäö")
                        .expires(Instant.now())
                        .build()
        );
    }


    @Test
    public void builder_valueIsEmptyString_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value("")
                        .expires(Instant.now())
                        .build()
        );
    }

    @Test
    public void builder_valueIsTooLong_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value(RandomUtil.getRandomString(2005))
                        .expires(Instant.now())
                        .build()
        );
    }

    @Test
    public void builder_valueIsNull_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ResponseCookie.builder()
                        .name("my_name")
                        .value(null)
                        .expires(Instant.now())
                        .build()
        );
    }

    @Test
    public void getHttpHeaderSetCookieString_HttpOnlyFalse_NoHttpOnlyFlag() {
        val httpHeaderSetCookieString = ResponseCookie.builder()
                .name("my_name")
                .value("my_value")
                .expires(Instant.now())
                .httpOnly(false)
                .build()
                .getHttpHeaderSetCookieString();
        assertThat(httpHeaderSetCookieString).doesNotContain("HttpOnly");
    }

    @Test
    public void getHttpHeaderSetCookieString_HttpOnlyNotSet_NoHttpOnlyFlag() {
        val httpHeaderSetCookieString = ResponseCookie.builder()
                .name("my_name")
                .value("my_value")
                .expires(Instant.now())
                .build()
                .getHttpHeaderSetCookieString();
        assertThat(httpHeaderSetCookieString).doesNotContain("HttpOnly");
    }


    @Test
    public void getHttpHeaderSetCookieString_HttpOnlyTrue_HttpOnlyPresent() {
        val httpHeaderSetCookieString = ResponseCookie.builder()
                .name("my_name")
                .value("my_value")
                .expires(Instant.now())
                .httpOnly(true)
                .build()
                .getHttpHeaderSetCookieString();
        assertThat(httpHeaderSetCookieString).contains("; HttpOnly");
    }

    @Test
    public void getHttpHeaderSetCookieString_SameSiteNotSet_NoSameSiteFlag() {
        val httpHeaderSetCookieString = ResponseCookie.builder()
                .name("my_name")
                .value("my_value")
                .expires(Instant.now())
                .build()
                .getHttpHeaderSetCookieString();
        assertThat(httpHeaderSetCookieString).doesNotContain("SameSite");
    }


    @Test
    public void getHttpHeaderSetCookieString_SameSiteNone_SameSiteSetToNone() {
        val httpHeaderSetCookieString = ResponseCookie.builder()
                .name("my_name")
                .value("my_value")
                .expires(Instant.now())
                .sameSite(SameSite.NONE)
                .build()
                .getHttpHeaderSetCookieString();
        assertThat(httpHeaderSetCookieString).contains("; SameSite=None");
    }

    @Test
    public void getHttpHeaderSetCookieString_SameSiteLax_SameSiteSetToLax() {
        val httpHeaderSetCookieString = ResponseCookie.builder()
                .name("my_name")
                .value("my_value")
                .expires(Instant.now())
                .sameSite(SameSite.LAX)
                .build()
                .getHttpHeaderSetCookieString();
        assertThat(httpHeaderSetCookieString).contains("; SameSite=Lax");
    }

    @Test
    public void getHttpHeaderSetCookieString_SameSiteStrict_SameSiteSetToStrict() {
        val httpHeaderSetCookieString = ResponseCookie.builder()
                .name("my_name")
                .value("my_value")
                .expires(Instant.now())
                .sameSite(SameSite.STRICT)
                .build()
                .getHttpHeaderSetCookieString();
        assertThat(httpHeaderSetCookieString).contains("; SameSite=Strict");
    }

}