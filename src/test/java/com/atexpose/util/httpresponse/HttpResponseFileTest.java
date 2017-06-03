package com.atexpose.util.httpresponse;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class HttpResponseFileTest {
    @Test
    public void getResponse_JsFile_HeaderShouldContainStatusCode200() {
        String fileContent = "This is file content";
        String httpResponse = UTF8.getString(HttpResponseFile.builder()
                .filename("monkey.js")
                .body(UTF8.getBytes(fileContent))
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.OK.getCode();
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_JsFile_HeaderContentTypeShouldBeJs() {
        String fileContent = "This is file content";
        String httpResponse = UTF8.getString(HttpResponseFile.builder()
                .filename("monkey.js")
                .body(UTF8.getBytes(fileContent))
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("Content-Type: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = ContentType.JAVASCRIPT.getContentType();
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_JsFileCustomHeader_HeaderShouldContainCustomHeader() {
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("key", "val")
                .build();
        String fileContent = "This is file content";
        String httpResponse = UTF8.getString(HttpResponseFile.builder()
                .filename("monkey.js")
                .customHeaders(customHeaders)
                .body(UTF8.getBytes(fileContent))
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("key: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "val";
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_JsFileCustomHeader_HeaderContentTypeShouldBeJs() {
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("key", "val")
                .build();
        String fileContent = "This is file content";
        String httpResponse = UTF8.getString(HttpResponseFile.builder()
                .filename("monkey.js")
                .customHeaders(customHeaders)
                .body(UTF8.getBytes(fileContent))
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("Content-Type: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = ContentType.JAVASCRIPT.getContentType();
        assertEquals(expected, actual);
    }


}