package com.atexpose.util.httpresponse;


import lombok.Getter;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-05-31.
 */
public enum HttpStatusCode {
    OK("200 OK"),
    REDIRECT("302"),
    FILE_NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");
    @Getter private final String code;


    HttpStatusCode(String code) {
        this.code = code;
    }
}
