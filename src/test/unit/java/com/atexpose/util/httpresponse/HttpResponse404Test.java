package com.atexpose.util.httpresponse;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class HttpResponse404Test {


    @Test
    public void getResponse_Default_HeaderShouldContainStatusCode404() {
        String httpResponse = UTF8.getString(HttpResponse404.builder()
                .filenameMissingFile("monkey.txt")
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.FILE_NOT_FOUND.getCode();
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_Default_BodyShouldContainFileName() {
        String httpResponse = UTF8.getString(HttpResponse404.builder()
                .filenameMissingFile("monkey.txt")
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("\r\n\r\n")
                .toString();
        String expected = "<html><body><center>File 'monkey.txt' not found</center><body></html>";
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_CustomHeader_HeaderShouldContainCustomHeader() {
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("key", "val")
                .build();
        String httpResponse = UTF8.getString(HttpResponse404.builder()
                .filenameMissingFile("monkey.txt")
                .customHeaders(map)
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("key: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "val";
        assertEquals(expected, actual);
    }
}