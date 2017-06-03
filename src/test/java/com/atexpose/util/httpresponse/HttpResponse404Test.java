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
        String header = UTF8.getString(HttpResponse404.builder()
                .filenameMissingFile("monkey.txt")
                .build()
                .getResponse());
        String actual = SubString.create(header)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.FILE_NOT_FOUND.getCode();
        assertEquals(expected, actual);
    }

    @Test
    public void getResponse_Default_BodyShouldContainFileName() {
        String header = UTF8.getString(HttpResponse404.builder()
                .filenameMissingFile("monkey.txt")
                .build()
                .getResponse());
        String actual = SubString.create(header)
                .startDelimiter("\r\n\r\n")
                .toString();
        String expected = "File 'monkey.txt' not found";
        assertEquals(expected, actual);
    }

    @Test
    public void getResponse_CustomHeader_HeaderShouldContainCustomHeader() {
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("key", "val")
                .build();
        String header = UTF8.getString(HttpResponse404.builder()
                .filenameMissingFile("monkey.txt")
                .customHeaders(map)
                .build()
                .getResponse());
        String actual = SubString.create(header)
                .startDelimiter("key: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "val";
        assertEquals(expected, actual);
    }
}