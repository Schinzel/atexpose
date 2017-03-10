package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.parser.urlparser.Redirect.RedirectType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author schinzel
 */
public class URLParserTest {

    //*************************************************************************
    //* POST
    //*************************************************************************
    public static final String POST_REQUEST_NORMAL = "POST /call/getDataFromPM HTTP/1.1\r\n"
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

    public static final String POST_REQUEST_ONE_SHORT_VARIABLE = "POST /call/getDataFromPM HTTP/1.1\r\n"
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

    public static final String POST_REQUEST_SHORT_METHODNAME = "POST /call/a HTTP/1.1\r\n"
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

    public static final String POST_REQUEST_LONG_METHODNAME = "POST /call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz HTTP/1.1\r\n"
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

    public static final String POST_REQUEST_NO_ARGS = "POST /call/a HTTP/1.1\r\n"
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

    public static final String POST_REQUEST_ONE_LINEBREAK = "POST /call/a HTTP/1.1\r\n"
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

    public static final String POST_REQUEST_NO_LINEBREAKS = "POST /call/a HTTP/1.1\r\n"
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

    //*************************************************************************
    //* GET
    //*************************************************************************
    public static final String GET_REQUEST_NORMAL = "GET /call/getDataFromPM?SSN=197107282222&Pin=88889 HTTP/1.1\r\n"
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

    public static final String GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK = "GET /call/getDataFromPM? HTTP/1.1\r\n"
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

    //Many variables
    public static final String GET_REQUEST_MANY_VARIABLES = "GET /call/getDataFromPM?a1=1&a2=2&a3=3&a4=4&a5=5&a6=6&a7=7&a8=8&a9=9&a10=10&a11=11&a12=12&a13=13&a14=14&a15=15 HTTP/1.1\r\n"
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

    //One variable
    public static final String GET_REQUEST_ONE_VARIABLE = "GET /call/getDataFromPM?SSN=197107282222 HTTP/1.1\r\n"
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

    //Short method name
    public static final String GET_REQUEST_SHORT_METHODNAME = "GET /call/a?SSN=197107282222 HTTP/1.1\r\n"
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

    //Long method name
    public static final String GET_REQUEST_LONG_METHODNAME = "GET /call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz?SSN=197107282222 HTTP/1.1\r\n"
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

    public static final String HTTP_HEADER_HEROKU_HTTPS = "GET /index.html?xyz=1234 HTTP/1.1\r\n"
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
            + "X-Forwarded-Proto: https\r\n"
            + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
            + "\r\n"
            + "The body\r\n";

    public static final String HTTP_HEADER_HEROKU_HTTP = "GET /index.html?xyz=1234 HTTP/1.1\r\n"
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
            + "X-Forwarded-Proto: http\r\n"
            + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
            + "\r\n"
            + "The body\r\n";

    public static final String HTTP_HEADER_HEROKU_HTTP_NO_QUERY = "GET /index123.html HTTP/1.1\r\n"
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
            + "X-Forwarded-Proto: http\r\n"
            + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
            + "\r\n"
            + "The body\r\n";

    public static final String HTTP_HEADER_REDIRECT_HOST = "GET /index123.html HTTP/1.1\r\n"
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
            + "X-Forwarded-Proto: http\r\n"
            + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
            + "\r\n"
            + "The body\r\n";

    public static final String HTTP_HEADER_REDIRECT_FILE = "GET /index123.html HTTP/1.1\r\n"
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
            + "X-Forwarded-Proto: http\r\n"
            + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
            + "\r\n"
            + "The body\r\n";

    public static final String HTTP_HEADER_REDIRECT_FILE_QUERY = "GET /index123.html?xyz=test HTTP/1.1\r\n"
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
            + "X-Forwarded-Proto: http\r\n"
            + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
            + "\r\n"
            + "The body\r\n";


    @Test
    public void testPostRequestParse() {
        String request;
        String actMethodName;
        String[] actArgNames, actArgValues;
        String expMethodName;
        String[] expArgNames, expArgValues;
        URLParser up;
        //
        up = new URLParser();
        request = POST_REQUEST_NORMAL;
        up.parseRequest(request);
        expMethodName = "getDataFromPM";
        expArgNames = new String[]{"name", "time"};
        expArgValues = new String[]{"John", "2pm"};
        actMethodName = up.getMethodName();
        actArgNames = up.getArgumentNames();
        actArgValues = up.getArgumentValues();
        assertEquals(expMethodName, actMethodName);
        assertArrayEquals(expArgNames, actArgNames);
        assertArrayEquals(expArgValues, actArgValues);
        //
        up = new URLParser();
        request = POST_REQUEST_ONE_SHORT_VARIABLE;
        up.parseRequest(request);
        expMethodName = "getDataFromPM";
        expArgNames = new String[]{"a"};
        expArgValues = new String[]{"1"};
        actMethodName = up.getMethodName();
        actArgNames = up.getArgumentNames();
        actArgValues = up.getArgumentValues();
        assertEquals(expMethodName, actMethodName);
        assertArrayEquals(expArgNames, actArgNames);
        assertArrayEquals(expArgValues, actArgValues);
        //
        up = new URLParser();
        request = POST_REQUEST_SHORT_METHODNAME;
        up.parseRequest(request);
        expMethodName = "a";
        expArgNames = new String[]{"name", "time"};
        expArgValues = new String[]{"John", "2pm"};
        actMethodName = up.getMethodName();
        actArgNames = up.getArgumentNames();
        actArgValues = up.getArgumentValues();
        assertEquals(expMethodName, actMethodName);
        assertArrayEquals(expArgNames, actArgNames);
        assertArrayEquals(expArgValues, actArgValues);
        //
        up = new URLParser();
        request = POST_REQUEST_LONG_METHODNAME;
        up.parseRequest(request);
        expMethodName = "abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz";
        expArgNames = new String[]{"name", "time"};
        expArgValues = new String[]{"John", "2pm"};
        actMethodName = up.getMethodName();
        actArgNames = up.getArgumentNames();
        actArgValues = up.getArgumentValues();
        assertEquals(expMethodName, actMethodName);
        assertArrayEquals(expArgNames, actArgNames);
        assertArrayEquals(expArgValues, actArgValues);
        //
        up = new URLParser();
        request = POST_REQUEST_NO_ARGS;
        up.parseRequest(request);
        expMethodName = "a";
        expArgNames = new String[0];
        expArgValues = new String[0];
        actMethodName = up.getMethodName();
        actArgNames = up.getArgumentNames();
        actArgValues = up.getArgumentValues();
        assertEquals(expMethodName, actMethodName);
        assertArrayEquals(expArgNames, actArgNames);
        assertArrayEquals(expArgValues, actArgValues);
        //
        up = new URLParser();
        request = POST_REQUEST_ONE_LINEBREAK;
        up.parseRequest(request);
        expMethodName = "a";
        expArgNames = new String[0];
        expArgValues = new String[0];
        actMethodName = up.getMethodName();
        actArgNames = up.getArgumentNames();
        actArgValues = up.getArgumentValues();
        assertEquals(expMethodName, actMethodName);
        assertArrayEquals(expArgNames, actArgNames);
        assertArrayEquals(expArgValues, actArgValues);
        //
        up = new URLParser();
        request = POST_REQUEST_NO_LINEBREAKS;
        up.parseRequest(request);
        expMethodName = "a";
        expArgNames = new String[0];
        expArgValues = new String[0];
        actMethodName = up.getMethodName();
        actArgNames = up.getArgumentNames();
        actArgValues = up.getArgumentValues();
        assertEquals(expMethodName, actMethodName);
        assertArrayEquals(expArgNames, actArgNames);
        assertArrayEquals(expArgValues, actArgValues);
    }


    @Test
    public void testGetFile() {
        URLParser instance = new URLParser();
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTPS);
        String expResult = "index.html";
        String result = instance.getFileName();
        assertEquals(expResult, result);
        //
        instance = new URLParser();
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP);
        expResult = "index.html";
        result = instance.getFileName();
        assertEquals(expResult, result);
        //
        instance = new URLParser();
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP_NO_QUERY);
        expResult = "index123.html";
        result = instance.getFileName();
        assertEquals(expResult, result);
    }


    @Test
    public void testToRedirect() {
        //No data
        boolean toRedirectIfHttp = true;
        URLParser instance = new URLParser(toRedirectIfHttp);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTPS);
        Pair<String, RedirectHttpStatus> result = instance.getRedirect();
        assertNull(result);
        //
        List<Redirect> redirects = new ArrayList<>();
        redirects.add(new Redirect("index.html", "1.thml", RedirectType.FILE, RedirectHttpStatus.TEMPORARY));
        instance = new URLParser(toRedirectIfHttp, redirects);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP);
        result = instance.getRedirect();
        assertNotNull(result);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP_NO_QUERY);
        result = instance.getRedirect();
        assertNull(result);
    }


    @Test
    public void testGetRedirectFile() {
        boolean toRedirectIfHttp = true;
        List<Redirect> redirects = new ArrayList<>();
        redirects.add(new Redirect("index.html", "1.html", RedirectType.FILE, RedirectHttpStatus.TEMPORARY));
        redirects.add(new Redirect("index123.html", "123.html", RedirectType.FILE, RedirectHttpStatus.TEMPORARY));
        //
        URLParser instance = new URLParser(toRedirectIfHttp, redirects);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTPS);
        String expResult = "1.html?xyz=1234";
        Pair<String, RedirectHttpStatus> redirect = instance.getRedirect();
//        String result = instance.getRedirectFile();
        assertEquals(expResult, redirect.getLeft());
        //
        instance = new URLParser(toRedirectIfHttp, redirects);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP);
        expResult = "1.html?xyz=1234";
        redirect = instance.getRedirect();
        //result = instance.getRedirectFile();
        assertEquals(expResult, redirect.getLeft());
        //
        instance = new URLParser(toRedirectIfHttp, redirects);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP_NO_QUERY);
        expResult = "123.html";
        redirect = instance.getRedirect();
        //result = instance.getRedirectFile();
        assertEquals(expResult, redirect.getLeft());
    }


    @Test
    public void testToRedirectToHttps() {
        boolean toRedirectIfHttp = true;
        URLParser instance = new URLParser(toRedirectIfHttp);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTPS);
        boolean expResult = false;
        boolean result = instance.toRedirectToHttps();
        assertEquals(expResult, result);
        //
        toRedirectIfHttp = true;
        instance = new URLParser(toRedirectIfHttp);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP);
        expResult = true;
        result = instance.toRedirectToHttps();
        assertEquals(expResult, result);
        //
        toRedirectIfHttp = false;
        instance = new URLParser(toRedirectIfHttp);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP);
        expResult = false;
        result = instance.toRedirectToHttps();
        assertEquals(expResult, result);
        //
        toRedirectIfHttp = false;
        instance = new URLParser(toRedirectIfHttp);
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTPS);
        expResult = false;
        result = instance.toRedirectToHttps();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetUrlWithHttps() {
        URLParser instance = new URLParser();
        instance.parseRequest(HTTP_HEADER_HEROKU_HTTP);
        String result = instance.getUrlWithHttps();
        String expResult = "https://127.0.0.1:5555/index.html?xyz=1234";
        assertEquals(expResult, result);
    }


    @Test
    public void testRedirectHost() {
        List<Redirect> redirects = new ArrayList<>();
        redirects.add(new Redirect("127.0.0.1:5555", "localhost:5555", RedirectType.HOST, RedirectHttpStatus.TEMPORARY));
        URLParser instance = new URLParser(false, redirects);
        instance.parseRequest(HTTP_HEADER_REDIRECT_HOST);
        assertTrue(instance.isRedirect());
        Pair<String, RedirectHttpStatus> info = instance.getRedirect();
        assertEquals("http://localhost:5555/index123.html", info.getLeft());
        assertEquals(RedirectHttpStatus.TEMPORARY, info.getRight());
    }


    @Test
    public void testRedirectFile() {
        List<Redirect> redirects = new ArrayList<>();
        redirects.add(new Redirect("index123.html", "my_redirect.html", RedirectType.FILE, RedirectHttpStatus.TEMPORARY));
        URLParser instance = new URLParser(false, redirects);
        instance.parseRequest(HTTP_HEADER_REDIRECT_FILE);
        assertTrue(instance.isRedirect());
        Pair<String, RedirectHttpStatus> info = instance.getRedirect();
        assertEquals("my_redirect.html", info.getLeft());
        assertEquals(RedirectHttpStatus.TEMPORARY, info.getRight());
    }


    @Test
    public void testRedirectFileQuery() {
        List<Redirect> redirects = new ArrayList<>();
        redirects.add(new Redirect("index123.html", "my_redirect.html", RedirectType.FILE, RedirectHttpStatus.TEMPORARY));
        URLParser instance = new URLParser(false, redirects);
        instance.parseRequest(HTTP_HEADER_REDIRECT_FILE_QUERY);
        assertTrue(instance.isRedirect());
        Pair<String, RedirectHttpStatus> info = instance.getRedirect();
        assertEquals("my_redirect.html?xyz=test", info.getLeft());
        assertEquals(RedirectHttpStatus.TEMPORARY, info.getRight());
    }

}
