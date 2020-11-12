package com.atexpose.dispatcher.channels.webchannel;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The purpose of this class is to generate the string for setting a cookie in a HTTP header
 * response.
 * <p>
 * Example:
 * "Set-Cookie: cookie_name=cookie_value; Path=/; Expires=Thu, 12 Nov 2020 04:39:51 GMT"
 */
@Accessors(prefix = "m")
public class WebSessionCookie {
    private static final DateTimeFormatter COOKIE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

    @Getter
    private final String mHttpHeaderSetCookieString;

    @Builder
    WebSessionCookie(String name, String value, Instant expires) {
        val cookieExpiresString = WebSessionCookie.getExpiresDate(expires);
        mHttpHeaderSetCookieString = "Set-Cookie: "
                + name + "=" + value + "; "
                + "Path=/; "
                + "Expires=" + cookieExpiresString + "\r\n";
    }

    static String getExpiresDate(Instant instant) {
        return LocalDateTime
                .ofInstant(instant, ZoneId.of("GMT"))
                .format(COOKIE_TIME_FORMAT);
    }
}

