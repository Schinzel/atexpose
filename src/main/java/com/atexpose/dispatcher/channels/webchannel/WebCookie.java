package com.atexpose.dispatcher.channels.webchannel;

import io.schinzel.basicutils.thrower.Thrower;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to generate the string for setting a cookie in a HTTP header
 * response.
 * <p>
 * Example:
 * "Set-Cookie: cookie_name=cookie_value; Path=/; Expires=Thu, 12 Nov 2020 04:39:51 GMT"
 */
@Accessors(prefix = "m")
public class WebCookie {
    private static final DateTimeFormatter COOKIE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

    private static final Pattern ALLOWED_CHARS_NAME_VALUE = Pattern
            .compile("[a-zA-Z0-9_-]{1,100}");


    @Getter
    private final String mHttpHeaderSetCookieString;

    @Builder
    WebCookie(String name, String value, Instant expires) {
        Thrower.createInstance()
                .throwIfVarEmpty(name, "name")
                .throwIfVarEmpty(value, "value")
                .throwIfNotMatchesRegex(name, "name", ALLOWED_CHARS_NAME_VALUE)
                .throwIfNotMatchesRegex(value, "value", ALLOWED_CHARS_NAME_VALUE)
                .throwIfTrue(expires.isBefore(Instant.now().minusSeconds(1)), "Expires has to be after now");
        val expiresAsString = WebCookie.getExpiresAsString(expires);
        mHttpHeaderSetCookieString = "Set-Cookie: "
                + name + "=" + value + "; "
                + "Path=/; "
                + "Expires=" + expiresAsString + "\r\n";
    }


    static String getExpiresAsString(Instant instant) {
        return LocalDateTime
                .ofInstant(instant, ZoneId.of("GMT"))
                .format(COOKIE_TIME_FORMAT);
    }
}

