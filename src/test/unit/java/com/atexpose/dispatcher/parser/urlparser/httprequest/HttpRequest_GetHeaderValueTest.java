package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class HttpRequest_GetHeaderValueTest {


    @Test
    public void getHeaderValue_Pragma_NoCache() {
        String headerValue = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getHeaderValue("Pragma");
        assertThat(headerValue).isEqualTo("no-cache");
    }


    @Test
    public void getHeaderValue_NonExistingHeader_EmptyString() {
        String headerValue = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getHeaderValue("I_do_not_exist");
        assertThat(headerValue).isEqualTo("");
    }


    @Test
    public void getHeaderValue_EmptyHeaderName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getHeaderValue("")
        );
    }


    @Test
    public void getHeaderValue_NullHeaderName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getHeaderValue(null)
        );
    }


    @Test
    public void getHeaderValue_EmptyHeaderValue_EmptyString() {
        String headerValue = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getHeaderValue("Pragma2");
        assertThat(headerValue).isEqualTo("");
    }


    @Test
    public void getHeaderValue_LongHeaderValue_HeaderValue() {
        String headerValue = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getHeaderValue("Cookie");
        assertThat(headerValue).isEqualTo("db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0");
    }


    @Test
    public void getHeaderValue_Host_LocalHost() {
        String headerValue = new HttpRequest(HttpRequestsUtil.FILE_REQUEST_NO_QUERY).getHeaderValue("Host");
        assertThat(headerValue).isEqualTo("127.0.0.1:5555");
    }
}
