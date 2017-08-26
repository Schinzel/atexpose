package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequest_GetPathTest {

    @Test
    public void getPath_PostRequestNormal_Path() {
        String path = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getPath();
        assertThat(path).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getPath_GetRequestNormal_Path() {
        String path = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getPath();
        assertThat(path).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getPath_GetRequestNoVariablesButWithQuestionMark_Path() {
        String path = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getPath();
        assertThat(path).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getPath_PostRequestLongMethodName() {
        String path = new HttpRequest(HttpRequestsUtil.POST_REQUEST_LONG_METHOD_NAME).getPath();
        assertThat(path).isEqualTo("call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz");
    }


    @Test
    public void getPath_PostRequestShortMethodName() {
        String path = new HttpRequest(HttpRequestsUtil.POST_REQUEST_SHORT_METHOD_NAME).getPath();
        assertThat(path).isEqualTo("call/a");
    }
}
