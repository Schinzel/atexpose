package com.atexpose.util.httpresponse;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class HttpResponse302Test {

    @Test
    public void HttpResponse302_Default_HeaderShouldContainStatusCode302() {
        String header = HttpResponse302.builder()
                .location("monkey.html")
                .build()
                .getResponse();
        String actual = SubString.create(header)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.REDIRECT.getCode();
        assertEquals(expected, actual);
    }

    @Test
    public void HttpResponse302_SetLocation_HeaderShouldContainLocation(){
        String header = HttpResponse302.builder()
                .location("monkey.html")
                .build()
                .getResponse();
        String actual = SubString.create(header)
                .startDelimiter("Location: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "monkey.html";
        assertEquals(expected, actual);
    }

    @Test
    public void HttpResponse302_SetCustomerHeader_HeaderShouldContainLocation(){
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("Key", "Val")
                .build();
        String header = HttpResponse302.builder()
                .location("monkey.html")
                .customHeaders(map)
                .build()
                .getResponse();
        String actual = SubString.create(header)
                .startDelimiter("Location: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "monkey.html";
        assertEquals(expected, actual);
    }


    @Test
    public void HttpResponse302_SetCustomerHeader_HeaderShouldContainCustomHeader(){
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