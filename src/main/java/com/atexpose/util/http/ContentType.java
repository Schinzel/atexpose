package com.atexpose.util.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
enum ContentType {
    //Images
    ICO("image/ico"),
    PNG("image/png"),
    JPG("image/jpg"),
    GIF("image/gif"),
    SVG("image/svg+xml"),
    //Text files
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript"),
    TEXT_FILE("text/plain"),
    JSON("application/json; charset=UTF-8"),
    //Other
    MAP("text/plain"),
    PDF("application/pdf"),
    WOFF("application/font-woff"),
    WOFF2("application/font-woff2"),
    //Text return
    TEXT("text/html; charset=UTF-8");


    @Getter
    private final String contentType;


}
