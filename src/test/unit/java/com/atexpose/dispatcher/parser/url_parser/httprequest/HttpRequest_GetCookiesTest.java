package com.atexpose.dispatcher.parser.url_parser.httprequest;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class HttpRequest_GetCookiesTest {

    @Test
    public void getCookies_FileRequest_8Cookies() {
        Map<String, String> cookies = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getCookies();
        assertThat(cookies).hasSize(8);
    }


    @Test
    public void getCookies_FileRequest_HasAllCookies() {
        Map<String, String> cookies = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getCookies();
        assertThat(cookies).containsKeys("db", "ci", "__distillery", "_gat", "_ga", "ptl", "undefined", "cp");
    }


    @Test
    public void getCookies_FileRequest_() {
        Map<String, String> cookies = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getCookies();
        assertThat(cookies)
                .containsEntry("db", "19710101")
                .containsEntry("ci", "0733787878")
                .containsEntry("__distillery", "v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3")
                .containsEntry("_gat", "1")
                .containsEntry("_ga", "GA1.1.921947710.1426063424")
                .containsEntry("ptl", "0")
                .containsEntry("undefined", "1")
                .containsEntry("cp", "2");
    }


    @Test
    public void getCookies_NoCookieHeader_EmptyMap() {
        String request = "GET /index.html HTTP/1.1\r\n"
                + "Host: 127.0.0.1:5555\r\n"
                + "Connection: keep-alive\r\n"
                + "Cache-Control: no-cache\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
                + "Accept-Encoding: gzip, deflate, sdch\r\n"
                + "Accept-Language: en-US,en;q=0.8\r\n"
                + "\r\n"
                + "The body\r\n";
        Map<String, String> cookies = new HttpRequest(request).getCookies();
        assertThat(cookies).isNotNull().isEmpty();
    }


    @Test
    public void getCookies_EmptyCookieHeader_EmptyMap() {
        String request = "GET /index.html HTTP/1.1\r\n"
                + "Host: 127.0.0.1:5555\r\n"
                + "Connection: keep-alive\r\n"
                + "Cache-Control: no-cache\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
                + "Accept-Encoding: gzip, deflate, sdch\r\n"
                + "Accept-Language: en-US,en;q=0.8\r\n"
                + "Cookie: \r\n"
                + "\r\n"
                + "The body\r\n";
        Map<String, String> cookies = new HttpRequest(request).getCookies();
        assertThat(cookies).isNotNull().isEmpty();
    }
}
