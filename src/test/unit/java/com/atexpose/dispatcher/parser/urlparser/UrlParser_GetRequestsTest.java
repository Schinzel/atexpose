package com.atexpose.dispatcher.parser.urlparser;

import io.schinzel.basicutils.EmptyObjects;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UrlParser_GetRequestsTest {


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


    @Test
    public void getMethodName_Request_MethodName() {
        String methodName = new UrlParser().getRequest(GET_REQUEST_NORMAL).getMethodName();
        assertEquals("getDataFromPM", methodName);
    }


    @Test
    public void getMethodName_LongMethodName_MethodName() {
        String methodName = new UrlParser().getRequest(GET_REQUEST_LONG_METHODNAME).getMethodName();
        assertEquals("abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz", methodName);
    }


    @Test
    public void getArgumentNames_TwoArguments_ArgNamesLengthAndNames() {
        String[] argumentNames = new UrlParser().getRequest(GET_REQUEST_NORMAL).getArgumentNames();
        assertEquals(2, argumentNames.length);
        assertEquals("SSN", argumentNames[0]);
        assertEquals("Pin", argumentNames[1]);
    }


    @Test
    public void getArgumentValues_TwoArguments_ArgValuesLengthAndStrings() {
        String[] argumentValues = new UrlParser().getRequest(GET_REQUEST_NORMAL).getArgumentValues();
        assertEquals(2, argumentValues.length);
        assertEquals("197107282222", argumentValues[0]);
        assertEquals("88889", argumentValues[1]);
    }


    @Test
    public void getMethodName_RequestWithQuestionMarkButNoVariable_MethodName() {
        String methodName = new UrlParser().getRequest(GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getMethodName();
        assertEquals("getDataFromPM", methodName);
    }


    @Test
    public void getArgNames_RequestWithQuestionMarkButNoVariable_EmptyArray() {
        String[] argumentValues = new UrlParser().getRequest(GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getArgumentNames();
        assertArrayEquals(EmptyObjects.EMPTY_STRING_ARRAY, argumentValues);
    }


    @Test
    public void getArgValues_RequestWithQuestionMarkButNoVariable_EmptyArray() {
        String[] argumentValues = new UrlParser().getRequest(GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getArgumentValues();
        assertArrayEquals(EmptyObjects.EMPTY_STRING_ARRAY, argumentValues);
    }


    @Test
    public void getMethodName_ShortMethodName_MethodName() {
        String methodName = new UrlParser().getRequest(GET_REQUEST_SHORT_METHODNAME).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_OneVariable_OneElement() {
        String[] argumentNames = new UrlParser().getRequest(GET_REQUEST_ONE_VARIABLE).getArgumentNames();
        assertEquals(1, argumentNames.length);
        assertEquals("SSN", argumentNames[0]);
    }


    @Test
    public void getArgumentValues_OneVariable_OneElement() {
        String[] argumentValues = new UrlParser().getRequest(GET_REQUEST_ONE_VARIABLE).getArgumentValues();
        assertEquals(1, argumentValues.length);
        assertEquals("197107282222", argumentValues[0]);
    }


    @Test
    public void isFileRequest_MethodRequest_False() {
        boolean isFileRequest = new UrlParser().getRequest(GET_REQUEST_NORMAL).isFileRequest();
        assertEquals(false, isFileRequest);
    }


}
