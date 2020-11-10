package com.atexpose.dispatcher.channels.webchannel;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Accessors(prefix = "m")
public class WebSessionCookie {
    private static final DateTimeFormatter COOKIE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

    @Getter
    private final String mHttpHeaderSetCookieString;

    @Builder
    WebSessionCookie(String name, String value, Instant expires) {
        val cookieExpiresString = LocalDateTime
                .ofInstant(expires, ZoneId.of("GMT"))
                .format(COOKIE_TIME_FORMAT);
        mHttpHeaderSetCookieString = "Set-Cookie: "
                + name + "=" + value + "; "
                + "Path=/; "
                + "Expires=" + cookieExpiresString + "\r\n";
        System.out.println(mHttpHeaderSetCookieString);

    }
}


