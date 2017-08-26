package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class HttpRequest_GetUriTest {

    @Test
    public void getUri() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.HTTP_HEADER);
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
        HttpRequest request = new HttpRequest(HttpRequestsUtil.HTTP_HEADER_NO_QUERY_STRING);
        assertEquals("http://127.0.0.1:5555/index.html", request.getURI().toString());
    }


    @Test
    public void getUri_HttpHeaderNoQueryString_QueryEmptyString() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.HTTP_HEADER_NO_QUERY_STRING);
        assertEquals(null, request.getURI().getQuery());
    }
}
