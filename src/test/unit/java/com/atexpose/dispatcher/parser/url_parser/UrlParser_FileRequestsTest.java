package com.atexpose.dispatcher.parser.url_parser;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;


public class UrlParser_FileRequestsTest {
    private static final String HTTP_HEADER_HEROKU_HTTPS = "GET /index.html?xyz=1234 HTTP/1.1\r\n"
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

    private static final String HTTP_HEADER_HEROKU_HTTP = "GET /index.html?xyz=1234 HTTP/1.1\r\n"
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

    private static final String HTTP_HEADER_HEROKU_HTTP_NO_QUERY = "GET /index123.html HTTP/1.1\r\n"
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
    public void isFileRequest_HerokuHttpsFileRequest_True() {
        boolean isFileRequest = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTPS).isFileRequest();
        assertEquals(true, isFileRequest);
    }


    @Test
    public void fileName_HerokuHttpsFileRequestQueryString_NameOfFile() {
        String fileName = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTPS).getFileName();
        assertEquals("index.html", fileName);
    }


    @Test
    public void getArgumentNames_HerokuHttpsGetFileRequestQueryString_EmptyArray() {
        List<String> argumentNames = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTPS).getArgumentNames();
        assertThat(argumentNames).isEmpty();
    }


    @Test
    public void getArgumentValues_HerokuHttpsGetFileRequestQueryString_EmptyArray() {
        List<String> argumentValues = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTPS).getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }


    @Test
    public void fileName_HerokuHttpFileRequestQueryString_NameOfFile() {
        String fileName = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTP).getFileName();
        assertEquals("index.html", fileName);
    }


    @Test
    public void getArgumentNames_HerokuHttpGetFileRequestQueryString_EmptyArray() {
        List<String> argumentNames = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTP).getArgumentNames();
        assertThat(argumentNames).isEmpty();
    }


    @Test
    public void getArgumentValues_HerokuHttpGetFileRequestQueryString_EmptyArray() {
        List<String> argumentValues = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTP).getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }


    @Test
    public void fileName_HerokuHttpFileRequest_NameOfFile() {
        String fileName = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTP_NO_QUERY).getFileName();
        assertEquals("index123.html", fileName);
    }


    @Test
    public void getArgumentNames_HerokuHttpGetFileRequest_ArgName() {
        List<String> argumentNames = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTP_NO_QUERY).getArgumentNames();
        assertThat(argumentNames).isEmpty();
    }


    @Test
    public void getArgumentValues_HerokuHttpGetFileRequest_ArgName() {
        List<String> argumentValues = new UrlParser().getRequest(HTTP_HEADER_HEROKU_HTTP_NO_QUERY).getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }
}
