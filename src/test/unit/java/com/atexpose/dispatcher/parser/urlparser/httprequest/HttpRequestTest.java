/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


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

    private static final String HTTP_HEADER_NO_QUERY_STRING = "GET /index.html HTTP/1.1\r\n"
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


    private static final String POST_REQUEST_NORMAL = "POST /call/getDataFromPM HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 18\r\n"
            + "Accept: */*\r\n"
            + "Origin: http://127.0.0.1:5555\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1\r\n"
            + "\r\n"
            + "name=John&time=2pm";

    private static final String POST_REQUEST_SHORT_METHODNAME = "POST /call/a HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 18\r\n"
            + "Accept: */*\r\n"
            + "Origin: http://127.0.0.1:5555\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1\r\n"
            + "\r\n"
            + "name=John&time=2pm";


    private static final String POST_REQUEST_LONG_METHODNAME = "POST /call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 18\r\n"
            + "Accept: */*\r\n"
            + "Origin: http://127.0.0.1:5555\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1\r\n"
            + "\r\n"
            + "name=John&time=2pm";


    private static final String POST_REQUEST_ONE_SHORT_VARIABLE = "POST /call/getDataFromPM HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 18\r\n"
            + "Accept: */*\r\n"
            + "Origin: http://127.0.0.1:5555\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1\r\n"
            + "\r\n"
            + "a=1";

    private static final String POST_REQUEST_NO_ARGS = "POST /call/a HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 18\r\n"
            + "Accept: */*\r\n"
            + "Origin: http://127.0.0.1:5555\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1\r\n"
            + "\r\n"
            + "";

    private static final String GET_REQUEST_NORMAL = "GET /call/getDataFromPM?SSN=197107282222&Pin=88889 HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Accept: */*\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate, sdch\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _gat=1; cp=1; _ga=GA1.1.957030889.1423688797; ptl=2; undefined=1\r\n"
            + "";

    private static final String GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK = "GET /call/getDataFromPM? HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Accept: */*\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate, sdch\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _gat=1; cp=1; _ga=GA1.1.957030889.1423688797; ptl=2; undefined=1\r\n"
            + "";

    private static final String GET_REQUEST_SHORT_METHODNAME = "GET /call/a?SSN=197107282222 HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Accept: */*\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate, sdch\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _gat=1; cp=1; _ga=GA1.1.957030889.1423688797; ptl=2; undefined=1\r\n"
            + "";

    private static final String GET_REQUEST_LONG_METHODNAME = "GET /call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz?SSN=197107282222 HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Accept: */*\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate, sdch\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _gat=1; cp=1; _ga=GA1.1.957030889.1423688797; ptl=2; undefined=1\r\n"
            + "";


    private static final String POST_REQUEST_ONE_LINEBREAK = "POST /call/a HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 18\r\n"
            + "Accept: */*\r\n"
            + "Origin: http://127.0.0.1:5555\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1\r\n"
            + "";

    private static final String POST_REQUEST_NO_LINEBREAKS = "POST /call/a HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 18\r\n"
            + "Accept: */*\r\n"
            + "Origin: http://127.0.0.1:5555\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1"
            + "";

    private static final String GET_REQUEST_ONE_VARIABLE = "GET /call/getDataFromPM?SSN=197107282222 HTTP/1.1\r\n"
            + "Host: 127.0.0.1:5555\r\n"
            + "Connection: keep-alive\r\n"
            + "Accept: */*\r\n"
            + "X-Requested-With: XMLHttpRequest\r\n"
            + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
            + "Referer: http://127.0.0.1:5555/\r\n"
            + "Accept-Encoding: gzip, deflate, sdch\r\n"
            + "Accept-Language: en-US,en;q=0.8\r\n"
            + "Cookie: db=19710728; ci=+46733759593; _gat=1; cp=1; _ga=GA1.1.957030889.1423688797; ptl=2; undefined=1\r\n"
            + "";


    @Test
    public void testGetURI() {
        HttpRequest request = new HttpRequest(HTTP_HEADER);
        URI uri = request.getURI();
        assertEquals("http://127.0.0.1:5555/index.html?xyz=1234", uri.toString());
        assertEquals("http", uri.getScheme());
        assertEquals("127.0.0.1", uri.getHost());
        assertEquals(5555, uri.getPort());
        assertEquals("xyz=1234", uri.getQuery());
        assertEquals("/index.html", uri.getPath());
    }


    @Test
    public void getUri_HttpHeaderNoQueryString_UriToStringNoQuestionMarkAtEndOfUrl() {
        HttpRequest request = new HttpRequest(HTTP_HEADER_NO_QUERY_STRING);
        assertEquals("http://127.0.0.1:5555/index.html", request.getURI().toString());
    }


    @Test
    public void getUri_HttpHeaderNoQueryString_QueryEmptyString() {
        HttpRequest request = new HttpRequest(HTTP_HEADER_NO_QUERY_STRING);
        assertEquals(null, request.getURI().getQuery());
    }


    @Test
    public void testGetPath() {
        String request, expResult, result;
        //
        request = POST_REQUEST_NORMAL;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_SHORT_METHODNAME;
        expResult = "call/a";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NORMAL;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_SHORT_METHODNAME;
        expResult = "call/a";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz";
        result = new HttpRequest(request).getPath();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetURL() {
        String request, expResult, result;
        //
        request = POST_REQUEST_NORMAL;
        expResult = "call/getDataFromPM";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_SHORT_METHODNAME;
        expResult = "call/a";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NORMAL;
        expResult = "call/getDataFromPM?SSN=197107282222&Pin=88889";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        //
        request = GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "call/getDataFromPM?";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_SHORT_METHODNAME;
        expResult = "call/a?SSN=197107282222";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_LONG_METHODNAME;
        expResult = "call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz?SSN=197107282222";
        result = new HttpRequest(request).getURL();
        assertEquals(expResult, result);

    }


    @Test
    public void getVariables_PostRequestRequest() {
        Map<String, String> actual = new HttpRequest(POST_REQUEST_NORMAL).getVariables();
        assertThat(actual).extracting("name").contains("John");
        assertThat(actual).extracting("time").contains("2pm");
        assertThat(actual).hasSize(2);
    }


    @Test
    public void getVariables_GetRequest() {
        Map<String, String> actual = new HttpRequest(GET_REQUEST_NORMAL).getVariables();
        assertThat(actual).extracting("SSN").contains("197107282222");
        assertThat(actual).extracting("Pin").contains("88889");
        assertThat(actual).hasSize(2);
    }


    @Test
    public void getVariables_PostRequestNoArgsNormal_EmptyMap() {
        Map<String, String> actual = new HttpRequest(POST_REQUEST_NO_ARGS).getVariables();
        assertThat(actual).isEmpty();
    }


    @Test
    public void getVariables_PostRequestNoArgsOneLinebreak_EmptyMap() {
        Map<String, String> actual = new HttpRequest(POST_REQUEST_ONE_LINEBREAK).getVariables();
        assertThat(actual).isEmpty();
    }


    @Test
    public void getVariables_PostRequestNoArgsNoLineabreaks_EmptyMap() {
        Map<String, String> actual = new HttpRequest(POST_REQUEST_NO_LINEBREAKS).getVariables();
        assertThat(actual).isEmpty();
    }


 /*   @Test
    public void testGetVariablesAsString() {
        String request, expResult, result;
        //
        request = POST_REQUEST_NORMAL;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_SHORT_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_LONG_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_NO_ARGS;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_ONE_LINEBREAK;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_NO_LINEBREAKS;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NORMAL;
        expResult = "SSN=197107282222&Pin=88889";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_ONE_SHORT_VARIABLE;
        expResult = "a=1";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_SHORT_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_LONG_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_ONE_VARIABLE;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getVariablesAsString();
        assertEquals(expResult, result);
    }
*/


    @Test
    public void testGetVariablesAsMap() {
        String request;
        Map<String, String> result;
        HashMap<String, String> expResult;
        //
        request = POST_REQUEST_NORMAL;
        expResult = new HashMap<>();
        expResult.put("name", "John");
        expResult.put("time", "2pm");
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_LONG_METHODNAME;
        expResult = new HashMap<>();
        expResult.put("name", "John");
        expResult.put("time", "2pm");
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_NO_ARGS;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_ONE_LINEBREAK;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_NO_LINEBREAKS;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NORMAL;
        expResult = new HashMap<>();
        expResult.put("SSN", "197107282222");
        expResult.put("Pin", "88889");
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_ONE_SHORT_VARIABLE;
        expResult = new HashMap<>();
        expResult.put("a", "1");
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = new HashMap<>();
        result = new HttpRequest(request).getVariables();
        assertEquals(expResult, result);

    }

/*
    @Test
    public void testGetQueryString() {
        String request, expResult, result;
        //
        request = POST_REQUEST_NORMAL;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_SHORT_METHODNAME;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_LONG_METHODNAME;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NORMAL;
        expResult = "SSN=197107282222&Pin=88889";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        //
        request = GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_SHORT_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_LONG_METHODNAME;
        expResult = "SSN=197107282222";
        result = new HttpRequest(request).getQueryString();
        assertEquals(expResult, result);
    }
*/


    @Test
    public void testGetBody() {
        String request, expResult, result;
        //
        request = POST_REQUEST_NORMAL;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_SHORT_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_LONG_METHODNAME;
        expResult = "name=John&time=2pm";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_NO_ARGS;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_ONE_LINEBREAK;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = POST_REQUEST_NO_LINEBREAKS;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NORMAL;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_SHORT_METHODNAME;
        expResult = "";
        result = new HttpRequest(request).getBody();
        assertEquals(expResult, result);
        //
        request = GET_REQUEST_LONG_METHODNAME;
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
