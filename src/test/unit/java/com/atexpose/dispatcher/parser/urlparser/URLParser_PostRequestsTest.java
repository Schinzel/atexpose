package com.atexpose.dispatcher.parser.urlparser;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


public class URLParser_PostRequestsTest {

    private static final String POST_REQUEST_ONE_SHORT_VARIABLE = "POST /api/getDataFromPM HTTP/1.1\r\n"
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

    private static final String POST_REQUEST_SHORT_METHODNAME = "POST /api/a HTTP/1.1\r\n"
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


    private static final String POST_REQUEST_NO_ARGS = "POST /api/a HTTP/1.1\r\n"
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

    private static final String POST_REQUEST_ONE_LINEBREAK = "POST /api/a HTTP/1.1\r\n"
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

    private static final String POST_REQUEST_NO_LINEBREAKS = "POST /api/a HTTP/1.1\r\n"
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
        List<String> argumentNames = new UrlParser().getRequest(POST_REQUEST_ONE_SHORT_VARIABLE).getArgumentNames();
        assertThat(argumentNames).containsExactly("a");
    }


    @Test
    public void getArgumentValues_ShortArgValue_ArgumentValue() {
        List<String> argumentValues = new UrlParser().getRequest(POST_REQUEST_ONE_SHORT_VARIABLE).getArgumentValues();
        assertThat(argumentValues).containsExactly("1");
    }


    @Test
    public void getArgumentNames_TwoArguments_ArgumentName() {
        List<String> argumentNames = new UrlParser().getRequest(POST_REQUEST_SHORT_METHODNAME).getArgumentNames();
        assertThat(argumentNames).containsExactly("name", "time");
    }


    @Test
    public void getArgumentValues_TwoArguments_ArgumentValue() {
        List<String> argumentValues = new UrlParser().getRequest(POST_REQUEST_SHORT_METHODNAME).getArgumentValues();
        assertThat(argumentValues).containsExactly("John", "2pm");
    }


    @Test
    public void getMethodName_NoArgs_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_NO_ARGS).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_NoArgs_EmptyList() {
        List<String> argumentNames = new UrlParser().getRequest(POST_REQUEST_NO_ARGS).getArgumentNames();
        assertThat(argumentNames).isEmpty();
    }


    @Test
    public void getArgumentValues_NoArgs_EmptyList() {
        List<String> argumentValues = new UrlParser().getRequest(POST_REQUEST_NO_ARGS).getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }


    @Test
    public void getMethodName_OneLineBreakAfterHeader_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_ONE_LINEBREAK).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_OneLineBreakAfterHeader_EmptyList() {
        List<String> argumentNames = new UrlParser().getRequest(POST_REQUEST_ONE_LINEBREAK).getArgumentNames();
        assertThat(argumentNames).isEmpty();
    }


    @Test
    public void getArgumentValues_OneLineBreakAfterHeader_EmptyList() {
        List<String> argumentValues = new UrlParser().getRequest(POST_REQUEST_ONE_LINEBREAK).getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }


    @Test
    public void getMethodName_NoLineBreakAfterHeader_MethodName() {
        String methodName = new UrlParser().getRequest(POST_REQUEST_NO_LINEBREAKS).getMethodName();
        assertEquals("a", methodName);
    }


    @Test
    public void getArgumentNames_NoLineBreakAfterHeader_EmptyList() {
        List<String> argumentNames = new UrlParser().getRequest(POST_REQUEST_NO_LINEBREAKS).getArgumentNames();
        assertThat(argumentNames).isEmpty();
    }


    @Test
    public void getArgumentValues_NoLineBreakAfterHeader_EmptyList() {
        List<String> argumentValues = new UrlParser().getRequest(POST_REQUEST_NO_LINEBREAKS).getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }


}
