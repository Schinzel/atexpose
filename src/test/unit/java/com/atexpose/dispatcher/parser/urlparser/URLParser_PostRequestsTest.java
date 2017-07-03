package com.atexpose.dispatcher.parser.urlparser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class URLParser_PostRequestsTest {

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


    @Test
    public void getMethodName_ShortMethodName_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_SHORT_METHODNAME).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getMethodName_LongMethodName_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_SHORT_METHODNAME).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_ShortArgName_ArgumentName() {
        String[] argumentNames = new UrlParser().getRequest(POST_REQUEST_ONE_SHORT_VARIABLE).getArgumentNames();
        assertEquals("a", argumentNames[0]);
        assertEquals(1, argumentNames.length);
    }


    @Test
    public void getArgumentValues_ShortArgValue_ArgumentValue() {
        String[] argumentValues = new UrlParser().getRequest(POST_REQUEST_ONE_SHORT_VARIABLE).getArgumentValues();
        assertEquals("1", argumentValues[0]);
        assertEquals(1, argumentValues.length);
    }


    @Test
    public void getArgumentNames_TwoArguments_ArgumentName() {
        String[] argumentNames = new UrlParser().getRequest(POST_REQUEST_SHORT_METHODNAME).getArgumentNames();
        assertEquals("name", argumentNames[0]);
        assertEquals("time", argumentNames[1]);
        assertEquals(2, argumentNames.length);
    }


    @Test
    public void getArgumentValues_TwoArguments_ArgumentValue() {
        String[] argumentValues = new UrlParser().getRequest(POST_REQUEST_SHORT_METHODNAME).getArgumentValues();
        assertEquals("John", argumentValues[0]);
        assertEquals("2pm", argumentValues[1]);
        assertEquals(2, argumentValues.length);
    }


    @Test
    public void getMethodName_NoArgs_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_NO_ARGS).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_NoArgs_EmptyArray() {
        String[] argumentNames = new UrlParser().getRequest(POST_REQUEST_NO_ARGS).getArgumentNames();
        assertEquals(0, argumentNames.length);
    }


    @Test
    public void getArgumentValues_NoArgs_EmptyArray() {
        String[] argumentValues = new UrlParser().getRequest(POST_REQUEST_NO_ARGS).getArgumentValues();
        assertEquals(0, argumentValues.length);
    }


    @Test
    public void getMethodName_OneLineBreakAfterHeader_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_ONE_LINEBREAK).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_OneLineBreakAfterHeader_EmptyArray() {
        String[] argumentNames = new UrlParser().getRequest(POST_REQUEST_ONE_LINEBREAK).getArgumentNames();
        assertEquals(0, argumentNames.length);
    }


    @Test
    public void getArgumentValues_OneLineBreakAfterHeader_EmptyArray() {
        String[] argumentValues = new UrlParser().getRequest(POST_REQUEST_ONE_LINEBREAK).getArgumentValues();
        assertEquals(0, argumentValues.length);
    }


    @Test
    public void getMethodName_NoLineBreakAfterHeader_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_NO_LINEBREAKS).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_NoLineBreakAfterHeader_EmptyArray() {
        String[] argumentNames = new UrlParser().getRequest(POST_REQUEST_NO_LINEBREAKS).getArgumentNames();
        assertEquals(0, argumentNames.length);
    }


    @Test
    public void getArgumentValues_NoLineBreakAfterHeader_EmptyArray() {
        String[] argumentValues = new UrlParser().getRequest(POST_REQUEST_NO_LINEBREAKS).getArgumentValues();
        assertEquals(0, argumentValues.length);
    }


}
