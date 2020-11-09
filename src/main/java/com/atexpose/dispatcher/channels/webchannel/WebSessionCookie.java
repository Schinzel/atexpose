package com.atexpose.dispatcher.channels.webchannel;

import lombok.Builder;

import java.time.Instant;

@Builder
public class WebSessionCookie {
    private final String name;
    private final String value;
    private final Instant expires;

    /**
     * @return A string that can be used in a http header response to set a cookie
     */
    public String getSetCookieString() {
        return "Set-Cookie: " + name + "=" + value + "; Path=/; Expires=Fri, 13 Nov 2020 14:12:12 UTC; Max-Age=900\r\n";
    }
}