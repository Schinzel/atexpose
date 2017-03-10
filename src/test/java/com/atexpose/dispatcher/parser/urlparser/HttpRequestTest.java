/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atexpose.dispatcher.parser.urlparser;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author schinzel
 */
public class HttpRequestTest {

    private static final String HTTP_HEADER = "GET /index.html?xyz=1234 HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Pragma: no-cache\r\n"
            + "Pragma2:\r\n"
            + "Pragma3: This:is:the:value\r\n"
            + "Cache-Control: no-cache\r\n"
            + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Accept-Encoding: gzip, deflate, sdch\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
            + "\r\n"
            + "The body\r\n";


    @Test
    public void testGetPath() {
        String request, expResult, result;
        //
        request = URLParserTest.POST_REQUEST_NORMAL;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_SHORT_METHODNAME;
        expResult = "call/a";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NORMAL;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_SHORT_METHODNAME;
        expResult = "call/a";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetURL() {
        String request, expResult, result;
        //
        request = URLParserTest.POST_REQUEST_NORMAL;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_SHORT_METHODNAME;
        expResult = "call/a";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NORMAL;
        expResult = "call/getDataFromPM?SSN=197107282222&Pin=88889";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        //
        request = URLParserTest.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "call/getDataFromPM?";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_SHORT_METHODNAME;
        expResult = "call/a?SSN=197107282222";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz?SSN=197107282222";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetVariablesAsString() {
        String request, expResult, result;
        //
        request = URLParserTest.POST_REQUEST_NORMAL;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_SHORT_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_LONG_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_NO_ARGS;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_ONE_LINEBREAK;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_NO_LINEBREAKS;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NORMAL;
        expResult = "SSN=197107282222&Pin=88889";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_ONE_SHORT_VARIABLE;
        expResult = "a=1";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_SHORT_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_LONG_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_MANY_VARIABLES;
        expResult = "a1=1&a2=2&a3=3&a4=4&a5=5&a6=6&a7=7&a8=8&a9=9&a10=10&a11=11&a12=12&a13=13&a14=14&a15=15";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_ONE_VARIABLE;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetVariablesAsMap() {
        String request;
        Map<String, String> result;
        HashMap<String, String> expResult;
        //
        request = URLParserTest.POST_REQUEST_NORMAL;
        expResult = new HashMap<>();
        expResult.put("name", "John");
        expResult.put("time", "2pm");
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_LONG_METHODNAME;
        expResult = new HashMap<>();
        expResult.put("name", "John");
        expResult.put("time", "2pm");
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_NO_ARGS;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_ONE_LINEBREAK;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_NO_LINEBREAKS;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NORMAL;
        expResult = new HashMap<>();
        expResult.put("SSN", "197107282222");
        expResult.put("Pin", "88889");
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_ONE_SHORT_VARIABLE;
        expResult = new HashMap<>();
        expResult.put("a", "1");
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_MANY_VARIABLES;
        expResult = new HashMap<>();
        expResult.put("a1", "1");
        expResult.put("a2", "2");
        expResult.put("a3", "3");
        expResult.put("a4", "4");
        expResult.put("a5", "5");
        expResult.put("a6", "6");
        expResult.put("a7", "7");
        expResult.put("a8", "8");
        expResult.put("a9", "9");
        expResult.put("a10", "10");
        expResult.put("a11", "11");
        expResult.put("a12", "12");
        expResult.put("a13", "13");
        expResult.put("a14", "14");
        expResult.put("a15", "15");
        result = new HttpRequest(request).getVariablesAsMap();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetQueryString() {
        String request, expResult, result;
        //
        request = URLParserTest.POST_REQUEST_NORMAL;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_SHORT_METHODNAME;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_LONG_METHODNAME;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NORMAL;
        expResult = "SSN=197107282222&Pin=88889";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        //
        request = URLParserTest.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_SHORT_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_LONG_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetBody() {
        String request, expResult, result;
        //
        request = URLParserTest.POST_REQUEST_NORMAL;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_SHORT_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_LONG_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_NO_ARGS;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_ONE_LINEBREAK;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.POST_REQUEST_NO_LINEBREAKS;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NORMAL;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_SHORT_METHODNAME;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = URLParserTest.GET_REQUEST_LONG_METHODNAME;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetHeader() {
        String headerName;
        String expResult;
        String result;
        HttpRequest instance = new HttpRequest(HTTP_HEADER);
        //Standard
        headerName = "Pragma";
        expResult = "no-cache";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
        //Non existing header
        headerName = "dkfjhdskjfhkdsjh";
        expResult = "";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
        //Empty header name
        headerName = "";
        expResult = "";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
        //Null header name
        headerName = null;
        expResult = "";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
        //Empty value
        headerName = "Pragma2";
        expResult = "";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
        //With colon in value
        headerName = "Pragma3";
        expResult = "This:is:the:value";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
        //Long value
        headerName = "Cookie";
        expResult = "db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
        //Host
        headerName = "Host";
        expResult = "127.0.0.1:5555";
        result = instance.getRequestHeaderValue(headerName);
        assertEquals(expResult, result);
    }


}
