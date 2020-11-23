package com.atexpose.util.web_cookie;

import lombok.Builder;
import lombok.Getter;

/**
 * The purpose of this call is to hold a cookie that was a part of the request
 */
@Builder
public class RequestCookie {
    @Getter private final String value;
}