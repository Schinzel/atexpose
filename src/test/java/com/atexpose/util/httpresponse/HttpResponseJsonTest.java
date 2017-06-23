package com.atexpose.util.httpresponse;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.substring.SubString;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseJsonTest {
    @Test
    public void getResponse_SetTwoJsonObjects_BodyShouldHaveFirstValue() {
        JSONObject json = new JSONObject(new LinkedHashMap<>());
        json.put("key1", "val1").put("key2", "val2");
        String response = HttpResponseJson.builder()
                .body(json)
                .build()
                .getResponse();
        String body = SubString.create(response)
                .startDelimiter("\r\n\r\n").getString();
        JSONObject jsonFromBody = new JSONObject(body);
        Assert.assertEquals(json.get("key1"), jsonFromBody.get("key1"));
    }


    @Test
    public void getResponse_SetTwoJsonObjects_BodyShouldHaveSecondValue() {
        JSONObject json = new JSONObject(new LinkedHashMap<>());
        json.put("key1", "val1").put("key2", "val2");
        String response = HttpResponseJson.builder()
                .body(json)
                .build()
                .getResponse();
        String body = SubString.create(response)
                .startDelimiter("\r\n\r\n").getString();
        JSONObject jsonFromBody = new JSONObject(body);
        Assert.assertEquals(json.get("key2"), jsonFromBody.get("key2"));
    }


    @Test
    public void getResponse_SetCustomHeaderAndTwoJsonObjects_BodyShouldHaveTwoJsonObjects() {
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("key", "val")
                .build();
        JSONObject json = new JSONObject(new LinkedHashMap<>());
        json.put("key1", "val1").put("key2", "val2");
        String response = HttpResponseJson.builder()
                .body(json)
                .customHeaders(customHeaders)
                .build()
                .getResponse();
        String body = SubString.create(response)
                .startDelimiter("\r\n\r\n").getString();
        JSONObject jsonFromBody = new JSONObject(body);
        Assert.assertEquals(json.length(), jsonFromBody.length());
    }


    @Test
    public void getResponse_SetCustomHeaderAndTwoJsonObjects_HeaderShouldHaveCustomHeader() {
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("key", "val")
                .build();
        JSONObject json = new JSONObject(new LinkedHashMap<>());
        json.put("key1", "val1").put("key2", "val2");
        String response = HttpResponseJson.builder()
                .body(json)
                .customHeaders(customHeaders)
                .build()
                .getResponse();
        String actual = SubString.create(response)
                .startDelimiter("key: ")
                .endDelimiter("\r\n\r\n").getString();
        String expected = "val";
        Assert.assertEquals(expected, actual);
    }
}