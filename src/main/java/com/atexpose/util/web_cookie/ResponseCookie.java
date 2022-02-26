package com.atexpose.util.web_cookie;

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
public class ResponseCookie {
    private static final DateTimeFormatter COOKIE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
    private static final Pattern ALLOWED_CHARS_NAME = Pattern
            .compile("[a-zA-Z0-9_-]{1,100}");
    private static final Pattern ALLOWED_CHARS_VALUE = Pattern
            .compile("[a-zA-Z0-9#:\"_{}\\[\\]\\-, ]{1,2000}");



    @Getter
    private final String mHttpHeaderSetCookieString;

    @Builder
    ResponseCookie(String name, String value, Instant expires, boolean httpOnly, SameSite sameSite) {
        Thrower.createInstance()
                .throwIfVarEmpty(name, "name")
                .throwIfVarEmpty(value, "value")
                .throwIfNotMatchesRegex(name, "name", ALLOWED_CHARS_NAME)
                .throwIfNotMatchesRegex(value, "value", ALLOWED_CHARS_VALUE)
                .throwIfVarNull(expires, "expires");
        val expiresAsString = ResponseCookie.getExpiresAsString(expires);
        val httpOnlyString = httpOnly ? "; HttpOnly" : "";
        val sameSiteString = (sameSite != null)
                ? "; SameSite=" + sameSite.getAttributeValue()
                : "";
        mHttpHeaderSetCookieString = "Set-Cookie: "
                + name + "=" + value + "; "
                + "Path=/; "
                + "Expires=" + expiresAsString
                + httpOnlyString
                + sameSiteString
                + "\r\n";
    }


    static String getExpiresAsString(Instant instant) {
        return LocalDateTime
                .ofInstant(instant, ZoneId.of("GMT"))
                .format(COOKIE_TIME_FORMAT);
    }
}

