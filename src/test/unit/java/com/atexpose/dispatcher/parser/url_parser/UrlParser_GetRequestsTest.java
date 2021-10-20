package com.atexpose.dispatcher.parser.url_parser;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


public class UrlParser_GetRequestsTest {


    private static final String GET_REQUEST_NORMAL = "GET /api/getDataFromPM?SSN=197107282222&Pin=88889 HTTP/1.1\r\n"
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

    private static final String GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK = "GET /api/getDataFromPM? HTTP/1.1\r\n"
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


    private static final String GET_REQUEST_ONE_VARIABLE = "GET /api/getDataFromPM?SSN=197107282222 HTTP/1.1\r\n"
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

    private static final String GET_REQUEST_SHORT_METHODNAME = "GET /api/a?SSN=197107282222 HTTP/1.1\r\n"
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

    private static final String GET_REQUEST_LONG_METHODNAME = "GET /api/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz?SSN=197107282222 HTTP/1.1\r\n"
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
        List<String> argumentNames = new UrlParser().getRequest(GET_REQUEST_NORMAL).getArgumentNames();
        assertThat(argumentNames).containsExactly("SSN", "Pin");
    }


    @Test
    public void getArgumentValues_TwoArguments_ArgValuesLengthAndStrings() {
        List<String> argumentValues = new UrlParser().getRequest(GET_REQUEST_NORMAL).getArgumentValues();
        assertThat(argumentValues).containsExactly("197107282222", "88889");
    }


    @Test
    public void getMethodName_RequestWithQuestionMarkButNoVariable_MethodName() {
        String methodName = new UrlParser().getRequest(GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getMethodName();
        assertEquals("getDataFromPM", methodName);
    }


    @Test
    public void getArgNames_RequestWithQuestionMarkButNoVariable_EmptyList() {
        List<String> argumentNames = new UrlParser().getRequest(GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getArgumentNames();
        assertThat(argumentNames).isEmpty();
    }


    @Test
    public void getArgValues_RequestWithQuestionMarkButNoVariable_EmptyList() {
        List<String> argumentValues = new UrlParser().getRequest(GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }


    @Test
    public void getMethodName_ShortMethodName_MethodName() {
        String methodName = new UrlParser().getRequest(GET_REQUEST_SHORT_METHODNAME).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_OneVariable_OneElement() {
        List<String> argumentNames = new UrlParser().getRequest(GET_REQUEST_ONE_VARIABLE).getArgumentNames();
        assertThat(argumentNames).containsExactly("SSN");
    }


    @Test
    public void getArgumentValues_OneVariable_OneElement() {
        List<String> argumentValues = new UrlParser().getRequest(GET_REQUEST_ONE_VARIABLE).getArgumentValues();
        assertThat(argumentValues).containsExactly("197107282222");
    }


    @Test
    public void isFileRequest_MethodRequest_False() {
        boolean isFileRequest = new UrlParser().getRequest(GET_REQUEST_NORMAL).isFileRequest();
        assertEquals(false, isFileRequest);
    }


}
