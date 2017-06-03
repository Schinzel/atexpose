package com.atexpose.util.httpresponse;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.substring.SubString;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class HttpResponseStringTest {
    @Test
    public void getResponse_SetBody_HeaderShouldContainStatusCode200()  {
        String body = "If I say you have a nice body, would you hold it against me?";
        String httpResponse = HttpResponseString.builder()
                .body(body)
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("HTTP/1.1 ")
                .endDelimiter("\r\n")
                .toString();
        String expected = HttpStatusCode.OK.getCode();
        assertEquals(expected, actual);
    }

    @Test
    public void getResponse_SetBody_BodyShouldBeTheSame()  {
        String body = "If I say you have a nice body, would you hold it against me?";
        String httpResponse = HttpResponseString.builder()
                .body(body)
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("\r\n\r\n")
                .toString();
        String expected = body;
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_SetBodyAndCustomHeader_BodyShouldBeTheSame()  {
        String body = "If I say you have a nice body, would you hold it against me?";
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("monkey", "gibbon")
                .build();
        String httpResponse = HttpResponseString.builder()
                .body(body)
                .customHeaders(customHeaders)
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("\r\n\r\n")
                .toString();
        String expected = body;
        assertEquals(expected, actual);
    }


    @Test
    public void getResponse_SetBodyAndCustomHeader_HeaderShouldContainCustomHeader()  {
        String body = "If I say you have a nice body, would you hold it against me?";
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("monkey", "gibbon")
                .build();
        String httpResponse = HttpResponseString.builder()
                .body(body)
                .customHeaders(customHeaders)
                .build()
                .getResponse();
        String actual = SubString.create(httpResponse)
                .startDelimiter("monkey: ")
                .endDelimiter("\r\n")
                .toString();
        String expected = "gibbon";
        assertEquals(expected, actual);
    }

}