package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequest_GetBodyTest {
    @Test
    public void getBody_PostRequest_Body() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getBody();
        assertThat(body).isEqualTo("name=John&time=2pm");
    }


    @Test
    public void getBody_PostRequestNoArguments_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NO_ARGS).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_PostRequestOneLineBreakBetweenHeaderAndBody_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_ONE_LINEBREAK).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_PostRequestNoLineBreakBetweenHeaderAndBody_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NO_LINE_BREAKS).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_GetRequest_Body() {
        String body = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_GetRequestNoVariablesButWithQuestionMark_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getBody();
        assertThat(body).isEqualTo("");
    }

}
