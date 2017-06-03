package com.atexpose.util.httpresponse;

import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class HttpResponse500Test {
    @Test
    public void HttpResponse500_Default_HeaderShouldContainStatusCode500() {
        String header = HttpResponse500.builder()
                .body("Something is rotten in the state of Denmark")
                .build()
                .getResponse();
        String actual = SubString.create(header)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.INTERNAL_SERVER_ERROR.getCode();
        assertEquals(expected, actual);
    }


    @Test
    public void HttpResponse500_SetMessage_BodyShouldBeMessage() {
        String message = "Something is rotten in the state of Denmark";
        String header = HttpResponse500.builder()
                .body(message)
                .build()
                .getResponse();
        String actual = SubString.create(header)
                .startDelimiter("\r\n\r\n")
                .toString();
        String expected = message;
        assertEquals(expected, actual);
    }
}