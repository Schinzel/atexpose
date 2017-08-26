package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequest_GetUrlTest {


    @Test
    public void getUrl_PostRequestNormal_Url() {
        String url = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getURL();
        assertThat(url).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getUrl_PostRequestShortMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.POST_REQUEST_SHORT_METHOD_NAME).getURL();
        assertThat(url).isEqualTo("call/a");
    }


    @Test
    public void getUrl_PostRequestLongMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.POST_REQUEST_LONG_METHOD_NAME).getURL();
        assertThat(url).isEqualTo("call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz");
    }


    @Test
    public void getUrl_GetRequestNormal_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getURL();
        assertThat(url).isEqualTo("call/getDataFromPM?SSN=197107282222&Pin=88889");
    }


    @Test
    public void getUrl_GetRequestNoVarsButWithQuestionMake_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getURL();
        assertThat(url).isEqualTo("call/getDataFromPM?");
    }


    @Test
    public void getUrl_GetRequestShortMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_SHORT_METHODNAME).getURL();
        assertThat(url).isEqualTo("call/a?SSN=197107282222");
    }


    @Test
    public void getUrl_GetRequestLongMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_LONG_METHODNAME).getURL();
        assertThat(url).isEqualTo("call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz?SSN=197107282222");
    }
}
