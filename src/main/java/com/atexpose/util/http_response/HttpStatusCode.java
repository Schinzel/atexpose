package com.atexpose.util.http_response;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The http status codes.
 * <p>
 * Created by Schinzel on 2017-05-31.
 */
@AllArgsConstructor
public enum HttpStatusCode {
    OK("200 OK"),
    REDIRECT("302"),
    FILE_NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");
    @Getter private final String code;
}
