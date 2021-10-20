package com.atexpose.util.http_response;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class HttpHeaderTest {

    @Test
    public void getHeader_StatusCode200_200InHeader() {
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .contentType(ContentType.TEXT)
                .contentLength(123)
                .build()
                .getHeader()
                .asString();
        String actual = SubString.create(header)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.OK.getCode();
        assertEquals(expected, actual);
    }


    @Test
    public void getHeader_ContentTypeSVG_SVG() {
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .contentType(ContentType.SVG)
                .contentLength(123)
                .build()
                .getHeader()
                .asString();
        String actual = SubString.create(header)
                .startDelimiter("Content-Type: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = ContentType.SVG.getContentType();
        assertEquals(expected, actual);
    }


    @Test
    public void getHeader_BrowserCacheMaxAgeInSeconds567_567() {
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .contentType(ContentType.SVG)
                .contentLength(123)
                .browserCacheMaxAgeInSeconds(567)
                .build()
                .getHeader()
                .asString();
        String actual = SubString.create(header)
                .startDelimiter("Cache-Control: max-age=")
                .endDelimiter("\r\n")
                .toString();
        String expected = "567";
        assertEquals(expected, actual);
    }


    @Test
    public void getHeader_ContentLength123_123() {
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .contentType(ContentType.SVG)
                .contentLength(123)
                .browserCacheMaxAgeInSeconds(567)
                .build()
                .getHeader()
                .asString();
        String actual = SubString.create(header)
                .startDelimiter("Content-Length: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "123";
        assertEquals(expected, actual);
    }


    @Test
    public void getHeader_CustomHeadersKey1Val1Key2Val2GetFirstKey_Key1Val1() {
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("Key1", "Val1")
                .put("Key2", "Val2")
                .build();
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .contentType(ContentType.SVG)
                .contentLength(123)
                .customHeaders(customHeaders)
                .browserCacheMaxAgeInSeconds(567)
                .build()
                .getHeader()
                .asString();
        String actual = SubString.create(header)
                .startDelimiter("Key1: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "Val1";
        assertEquals(expected, actual);
    }


    @Test
    public void getHeader_CustomHeadersKey1Val1Key2Val2GetFirstKey_Key2Val2() {
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("Key1", "Val1")
                .put("Key2", "Val2")
                .build();
        String header = HttpHeader.builder()
                .httpStatusCode(HttpStatusCode.OK)
                .contentType(ContentType.SVG)
                .contentLength(123)
                .customHeaders(customHeaders)
                .browserCacheMaxAgeInSeconds(567)
                .build()
                .getHeader()
                .asString();
        String actual = SubString.create(header)
                .startDelimiter("Key2: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "Val2";
        assertEquals(expected, actual);
    }


}