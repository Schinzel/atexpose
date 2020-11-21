package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequest_GetUriTest {


    @Test
    public void getUri_GetScheme() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_ONE_QUERY_VARIABLE);
        String scheme = request.getURI().getScheme();
        assertThat(scheme).isEqualTo("http");

    }


    @Test
    public void getUri_GetHost() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_ONE_QUERY_VARIABLE);
        String host = request.getURI().getHost();
        assertThat(host).isEqualTo("127.0.0.1");
    }


    @Test
    public void getUri_GetPort() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_ONE_QUERY_VARIABLE);
        int port = request.getURI().getPort();
        assertThat(port).isEqualTo(5555);
    }


    @Test
    public void getUri_GetQuery() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_ONE_QUERY_VARIABLE);
        String query = request.getURI().getQuery();
        assertThat(query).isEqualTo("xyz=1234");
    }


    @Test
    public void getUri_GetPath() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_ONE_QUERY_VARIABLE);
        String path = request.getURI().getPath();
        assertThat(path).isEqualTo("/index.html");
    }


    @Test
    public void getUri_FileRequestOneVariable() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_ONE_QUERY_VARIABLE);
        String toString = request.getURI().toString();
        assertThat(toString).isEqualTo("http://127.0.0.1:5555/index.html?xyz=1234");
    }


    @Test
    public void getUri_FileRequestNoQueryString_UriToStringNoQuestionMarkAtEndOfUrl() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY);
        String toString = request.getURI().toString();
        assertThat(toString).isEqualTo("http://127.0.0.1:5555/index.html");
    }

}
