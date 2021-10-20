package com.atexpose.util.http_response;

import io.schinzel.basicutils.substring.SubString;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class HttpResponse500Test {
    @Test
    public void getResponse_Default_HeaderShouldContainStatusCode500() {
        JSONObject exceptionProperties = new JSONObject().put("message", "Something is rotten in the state of Denmark");
        String httpResponse = HttpResponse500.builder()
                .body(exceptionProperties)
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
        JSONObject exceptionProperties = new JSONObject().put("message", "Something is rotten in the state of Denmark");
        String httpResponse = HttpResponse500.builder()
                .body(exceptionProperties)
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("\r\n\r\n")
                .toString();
        assertEquals(exceptionProperties.toString() + "\n\n", actual);
    }
}