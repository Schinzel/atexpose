package com.atexpose.util.httpresponse;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class HttpResponse404Test {


    @Test
    public void getResponse_Default_HeaderShouldContainStatusCode404() {
        String httpResponse = UTF8.getString(HttpResponse404.builder()
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.FILE_NOT_FOUND.getCode();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void getResponse_Default_BodyShouldContainFileName() {
        String httpResponse = UTF8.getString(HttpResponse404.builder()
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("\r\n\r\n")
                .toString();
        String expected = "<html><body><center>File not found</center><body></html>";
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void getResponse_Custom404Page_Custom404PageShould() {
        String html404Page = "<html><body><center>Bummer! File not found :(</center><body></html>";
        String httpResponse = UTF8.getString(HttpResponse404.builder()
                .body(html404Page.getBytes(Charsets.UTF_8))
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("\r\n\r\n")
                .toString();
        assertThat(actual).isEqualTo(html404Page);
    }


    @Test
    public void getResponse_CustomHeader_HeaderShouldContainCustomHeader() {
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("key", "val")
                .build();
        String httpResponse = UTF8.getString(HttpResponse404.builder()
                .customHeaders(map)
                .build()
                .getResponse());
        String actual = SubString.create(httpResponse)
                .startDelimiter("key: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "val";
        assertThat(actual).isEqualTo(expected);
    }
}