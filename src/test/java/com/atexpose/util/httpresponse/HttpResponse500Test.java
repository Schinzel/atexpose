package com.atexpose.util.httpresponse;

import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class HttpResponse500Test {
    @Test
    public void getResponse_Default_HeaderShouldContainStatusCode500() {
        String httpResponse = HttpResponse500.builder()
                .body("Something is rotten in the state of Denmark")
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.INTERNAL_SERVER_ERROR.getCode();
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_SetMessage_BodyShouldBeMessage() {
        String message = "Something is rotten in the state of Denmark";
        String httpResponse = HttpResponse500.builder()
                .body(message)
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("\r\n\r\n")
                .toString();
        String expected = message;
        assertEquals(expected, actual);
    }
}