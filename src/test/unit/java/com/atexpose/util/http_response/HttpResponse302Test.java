package com.atexpose.util.http_response;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class HttpResponse302Test {

    @Test
    public void getResponse_Default_HeaderShouldContainStatusCode302() {
        String httpResponse = HttpResponse302.builder()
                .location("monkey.html")
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.REDIRECT.getCode();
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_SetLocation_HeaderShouldContainLocation() {
        String httpResponse = HttpResponse302.builder()
                .location("monkey.html")
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("Location: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "monkey.html";
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_SetCustomerHeader_HeaderShouldContainLocation() {
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("Key", "Val")
                .build();
        String httpResponse = HttpResponse302.builder()
                .location("monkey.html")
                .customHeaders(map)
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("Location: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "monkey.html";
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_SetCustomerHeader_HeaderShouldContainCustomHeader() {
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("Key", "Val")
                .build();
        String header = HttpResponse302.builder()
                .location("monkey.html")
                .customHeaders(map)
                .build()
                .getResponse();
        String actual = SubString.create(header)
                .startDelimiter("Key: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "Val";
        assertEquals(expected, actual);
    }
}